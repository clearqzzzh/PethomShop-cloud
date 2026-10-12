package com.kk.pethome.pay.controller;

import com.alibaba.fastjson.JSON;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.api.CouponFeignAPI;
import com.kk.pethome.api.OrderFeignAPI;
import com.kk.pethome.api.ProductFeignAPI;
import com.kk.pethome.pay.service.AlipayService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/pay")
public class AlipayController {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductFeignAPI productFeignAPI;

    @Autowired
    private OrderFeignAPI orderFeignAPI;

    @Autowired
    private CouponFeignAPI couponFeignAPI;

    /** 模拟支付：下单后直接确认支付，更新订单状态 + 发MQ扣库存 */
    @PostMapping("/checkout")
    @Operation(summary = "模拟支付确认")
    public jsonresult checkout(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("orderId").toString());
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        Long userId = body.get("userId") != null ? Long.valueOf(body.get("userId").toString()) : null;

        // 0. 先查订单获取 userId（兜底）+ couponId + orderSn
        Long couponId = null;
        String orderSn = null;
        try {
            jsonresult orderResult = orderFeignAPI.getOrderById(orderId);
            if (orderResult != null && orderResult.getSuccess() && orderResult.getData() != null) {
                Object data = orderResult.getData();
                if (data instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> orderData = (Map<String, Object>) data;
                    if (userId == null && orderData.get("userId") != null) {
                        userId = Long.valueOf(orderData.get("userId").toString());
                    }
                    Object couponIdObj = orderData.get("couponId");
                    if (couponIdObj != null) {
                        couponId = Long.valueOf(couponIdObj.toString());
                    }
                    Object orderSnObj = orderData.get("orderSn");
                    if (orderSnObj != null) {
                        orderSn = orderSnObj.toString();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("查询订单信息异常: {}", e.getMessage());
        }

        // 支付流水号：优先用 orderSn，兜底用 SIM + orderId
        String outTradeNo = (orderSn != null) ? orderSn : "SIM" + orderId;

        // 1. 通过 Feign 调用 order-service 更新订单状态（t_order 在 pethome_order 库）
        orderFeignAPI.payOrder(orderId);

        // 2. 记录支付流水
        jdbcTemplate.update(
            "INSERT INTO t_pay_log(user_id, order_id, out_trade_no, amount, status, pay_time, create_time) VALUES(?,?,?,?,1,NOW(),NOW())",
            userId, orderId, outTradeNo, amount
        );

        // 3. 核销优惠券（如果订单使用了优惠券）
        if (couponId != null && userId != null) {
            try {
                Map<String, Object> useBody = new HashMap<>();
                useBody.put("couponId", couponId);
                useBody.put("userId", userId);
                useBody.put("orderId", orderId);
                useBody.put("amount", amount.toString());
                couponFeignAPI.useCoupon(useBody);
                log.info("优惠券已核销: couponId={}, orderId={}", couponId, orderId);
            } catch (Exception e) {
                log.warn("优惠券核销异常（不影响支付流程）: {}", e.getMessage());
            }
        }

        // 4. 发MQ异步扣库存
        Map<String, Object> mqMsg = new HashMap<>();
        mqMsg.put("order_id", orderId);
        mqMsg.put("out_trade_no", outTradeNo);
        mqMsg.put("amount", amount);
        if (rocketMQTemplate != null) {
            rocketMQTemplate.convertAndSend("order_pay_success", JSON.toJSONString(mqMsg));
            log.info("支付消息已发MQ: orderId={}", orderId);
        } else {
            log.warn("MQ不可用，直接扣库存");
            // 通过 Feign 调用 product-service 扣库存（跨库调用）
            jsonresult stockResult = productFeignAPI.updateOrderAndStock(orderId);
            if (!stockResult.getSuccess()) {
                return new jsonresult(false, "库存扣减失败: " + stockResult.getMessage());
            }
        }

        return jsonresult.success(Map.of("orderId", orderId, "status", "paid"));
    }

    @Operation(summary = "创建支付宝支付订单，返回支付页面 HTML")
    @PostMapping("/create")
    public String createPayOrder(@RequestParam String orderId,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam(defaultValue = "宠物商城订单") String subject) {
        return alipayService.createPayOrder(orderId, amount, subject);
    }

    /**
     * 支付宝异步通知回调（不能加任何鉴权——由支付宝签名验证保证安全）
     */
    @Operation(summary = "支付宝支付异步通知（支付宝回调，无需鉴权）")
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> params.put(k, v[0]));

        log.info("收到支付宝异步通知: out_trade_no={}, trade_status={}, total_amount={}",
                params.get("out_trade_no"), params.get("trade_status"), params.get("total_amount"));

        // 1. 验证签名
        if (!alipayService.verifyCallback(params)) {
            log.error("支付宝异步通知签名验证失败");
            return "failure";
        }

        // 2. 只处理 TRADE_SUCCESS
        if (!"TRADE_SUCCESS".equals(params.get("trade_status"))) {
            log.info("非支付成功状态，忽略: {}", params.get("trade_status"));
            return "success";
        }

        // 3. 支付宝回调直接扣库存（MQ待RocketMQ5.x后启用）
        // out_trade_no 可能是 orderId（旧格式）或 orderSn（新格式 PH...）
        String outTradeNo = params.get("out_trade_no");
        Long orderIdForStock = null;
        try {
            if (outTradeNo != null && outTradeNo.startsWith("PH")) {
                // 新格式：orderSn，需要查订单获取 orderId
                // 通过 Feign 调 order-service 查订单
                // 这里暂时用 productFeignAPI 的接口不合适，直接 JDBC 查
                Long foundId = jdbcTemplate.queryForObject(
                    "SELECT id FROM t_order WHERE order_sn = ? LIMIT 1", Long.class, outTradeNo);
                orderIdForStock = foundId;
            } else if (outTradeNo != null) {
                // 旧格式：直接是 orderId
                orderIdForStock = Long.valueOf(outTradeNo);
            }
        } catch (Exception e) {
            log.error("解析 out_trade_no 失败: {}", outTradeNo, e);
        }

        if (orderIdForStock != null) {
            jsonresult stockResult = productFeignAPI.updateOrderAndStock(orderIdForStock);
            if (!stockResult.getSuccess()) {
                log.error("库存扣减失败: {}", stockResult.getMessage());
            } else {
                log.info("支付回调处理完成: out_trade_no={}", outTradeNo);
            }
        } else {
            log.error("无法解析 out_trade_no: {}", outTradeNo);
        }

        return "success";
    }
}
