package com.kk.pethome.order.service.impl;

import com.kk.pethome.api.CouponFeignAPI;
import com.kk.pethome.api.ProductFeignAPI;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.order.mapper.OrderMapper;
import com.kk.pethome.order.pojo.Order;
import com.kk.pethome.order.pojo.OrderItem;
import com.kk.pethome.order.service.DirectOrderService;
import com.kk.pethome.product.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 直接下单（不走购物车），给 Agent 工具调用 */
@Service
public class DirectOrderServiceImpl implements DirectOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductFeignAPI productFeignAPI;

    @Autowired
    private CouponFeignAPI couponFeignAPI;

    @Override
    @Transactional
    public Order createDirect(Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = Integer.parseInt(body.getOrDefault("quantity", "1").toString());
        String category = (String) body.get("category");
        String recipient = (String) body.get("recipient");
        String phone = (String) body.get("phone");
        String address = (String) body.get("address");

        // 通过 Feign 从 product-service 查询商品信息
        BigDecimal price = BigDecimal.ZERO;
        String name = "未知商品";
        try {
            jsonresult result = productFeignAPI.getDetail(productId);
            if (result != null && result.getSuccess() && result.getData() != null) {
                // 从 jsonresult 的 data 字段提取 Product
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Product product = mapper.convertValue(result.getData(), Product.class);
                if (product != null) {
                    name = product.getName() != null ? product.getName() : name;
                    price = product.getPrice() != null ? product.getPrice() : price;
                }
            }
        } catch (Exception e) {
            // Feign 调用失败，使用默认值
        }

        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));

        // 优惠券校验 + 折扣
        BigDecimal discountAmount = BigDecimal.ZERO;
        Long couponId = body.containsKey("couponId") && body.get("couponId") != null
            ? Long.valueOf(body.get("couponId").toString()) : null;
        if (couponId != null && couponId > 0) {
            try {
                Map<String, Object> checkBody = new HashMap<>();
                checkBody.put("userId", userId);
                checkBody.put("couponId", couponId);
                checkBody.put("orderAmount", total.toString());
                jsonresult checkResult = couponFeignAPI.checkAndReserve(checkBody);
                if (checkResult != null && checkResult.getSuccess()) {
                    Object data = checkResult.getData();
                    if (data instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> checkData = (Map<String, Object>) data;
                        Object discount = checkData.get("discountAmount");
                        if (discount != null) {
                            discountAmount = new BigDecimal(discount.toString());
                        }
                    }
                } else {
                    String msg = checkResult != null ? checkResult.getMessage() : "优惠券校验失败";
                    throw new RuntimeException(msg != null ? msg : "优惠券不可用");
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("优惠券服务异常: " + e.getMessage());
            }
        }

        Order order = new Order();
        order.setOrderSn(OrderSnGenerator.generate());
        order.setUserId(userId);
        order.setRecipient(recipient);
        order.setPhone(phone);
        order.setAddress(address);
        order.setTotalAmount(total.subtract(discountAmount));
        order.setStatus(0);
        order.setCreateTime(new Date());
        order.setCouponId(couponId != null && couponId > 0 ? couponId : null);
        order.setDiscountAmount(discountAmount);
        orderMapper.insertOrder(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setUserId(userId);
        item.setProductId(productId);
        item.setCategory(category);
        item.setName(name);
        item.setPrice(price);
        item.setQuantity(quantity);
        orderMapper.insertItems(List.of(item));

        order.setItems(List.of(item));
        return order;
    }
}
