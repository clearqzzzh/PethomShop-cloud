package com.kk.pethome.order.service.impl;

import com.kk.pethome.order.mapper.OrderMapper;
import com.kk.pethome.order.mapper.RefundLogMapper;
import com.kk.pethome.order.pojo.Order;
import com.kk.pethome.order.pojo.RefundLog;
import com.kk.pethome.order.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 退货退款服务
 * 退货状态流转：
 *   refund_status=0 无退货
 *   refund_status=1 待审核（用户申请退货）
 *   refund_status=2 已同意退货中（管理员通过 / Agent自动审批）
 *   refund_status=3 已拒绝
 *   refund_status=4 已退款（退货完成）
 * 订单状态：approveReturn时order.status→4(退货中)，completeRefund时order.status→5(已退款)
 */
@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RefundLogMapper refundLogMapper;

    /**
     * 检查退货资格
     * @return {eligible: bool, reason: string, order: ...}
     */
    @Override
    public Map<String, Object> checkEligibility(Long orderId) {
        Map<String, Object> result = new LinkedHashMap<>();
        Order order = orderMapper.lookById(orderId);
        if (order == null) {
            result.put("eligible", false);
            result.put("reason", "订单不存在");
            return result;
        }

        Integer status = order.getStatus();
        Integer refundStatus = order.getRefundStatus();

        // 检查订单状态
        if (status == null || status == 0) {
            result.put("eligible", false);
            result.put("reason", "订单未付款，无法退货");
            result.put("orderStatus", statusText(status));
            return result;
        }
        if (status >= 5) {
            result.put("eligible", false);
            result.put("reason", "订单已退款，无需重复操作");
            result.put("orderStatus", statusText(status));
            return result;
        }

        // 检查退货状态
        if (refundStatus != null && refundStatus == 1) {
            result.put("eligible", false);
            result.put("reason", "已有退货申请待审核，请勿重复提交");
            result.put("refundStatus", 1);
            return result;
        }
        if (refundStatus != null && refundStatus == 2) {
            result.put("eligible", false);
            result.put("reason", "退货申请已批准，正在处理中");
            result.put("refundStatus", 2);
            return result;
        }
        if (refundStatus != null && refundStatus == 4) {
            result.put("eligible", false);
            result.put("reason", "订单已退款完成");
            result.put("refundStatus", 4);
            return result;
        }

        // 可以退货
        result.put("eligible", true);
        result.put("reason", "符合退货条件");
        result.put("orderId", order.getId());
        result.put("orderSn", order.getOrderSn());
        result.put("orderStatus", statusText(status));
        result.put("totalAmount", order.getTotalAmount());
        return result;
    }

    /**
     * 申请退货（用户发起）
     */
    @Override
    @Transactional
    public Map<String, Object> applyForReturn(Long orderId, Long userId, String reason) {
        Map<String, Object> result = new LinkedHashMap<>();
        Order order = orderMapper.lookById(orderId);

        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        if (!order.getUserId().equals(userId)) {
            result.put("success", false);
            result.put("message", "只能操作自己的订单");
            return result;
        }

        Integer status = order.getStatus();
        if (status == null || status < 1 || status >= 5) {
            result.put("success", false);
            result.put("message", "订单状态不允许退货（当前：" + statusText(status) + "）");
            return result;
        }

        Integer refundStatus = order.getRefundStatus();
        if (refundStatus != null && refundStatus == 1) {
            result.put("success", false);
            result.put("message", "已有退货申请待审核");
            return result;
        }
        if (refundStatus != null && refundStatus >= 2 && refundStatus <= 4) {
            result.put("success", false);
            result.put("message", "退货已在处理中或已完成");
            return result;
        }

        // 更新订单退货状态为"待审核"
        orderMapper.updateRefundStatus(orderId, 1);

        // 创建退款记录
        RefundLog log = new RefundLog();
        log.setOrderId(orderId);
        log.setOrderSn(order.getOrderSn());
        log.setUserId(userId);
        log.setReason(reason != null ? reason : "用户申请退货");
        log.setRefundAmount(order.getTotalAmount());
        log.setStatus(1); // 待审核
        refundLogMapper.insert(log);

        result.put("success", true);
        result.put("message", "退货申请已提交，等待审核");
        result.put("refundLogId", log.getId());
        result.put("orderSn", order.getOrderSn());
        result.put("refundAmount", order.getTotalAmount());
        return result;
    }

    /**
     * 审核通过退货（管理员 / Agent自动审批）
     */
    @Override
    @Transactional
    public Map<String, Object> approveReturn(Long orderId, String adminRemark) {
        Map<String, Object> result = new LinkedHashMap<>();
        Order order = orderMapper.lookById(orderId);

        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        if (order.getRefundStatus() == null || order.getRefundStatus() != 1) {
            result.put("success", false);
            result.put("message", "订单没有待审核的退货申请");
            return result;
        }

        // 更新为"已同意退货中"
        orderMapper.updateRefundStatus(orderId, 2);
        // 更新订单状态为"退货中"
        orderMapper.returnOrder(orderId);

        // 更新退款记录
        RefundLog log = refundLogMapper.findByOrderId(orderId);
        if (log != null) {
            refundLogMapper.updateStatus(log.getId(), 2, adminRemark);
        }

        result.put("success", true);
        result.put("message", "退货申请已批准");
        result.put("orderSn", order.getOrderSn());
        return result;
    }

    /**
     * 审核拒绝退货
     */
    @Override
    @Transactional
    public Map<String, Object> rejectReturn(Long orderId, String adminRemark) {
        Map<String, Object> result = new LinkedHashMap<>();
        Order order = orderMapper.lookById(orderId);

        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        if (order.getRefundStatus() == null || order.getRefundStatus() != 1) {
            result.put("success", false);
            result.put("message", "订单没有待审核的退货申请");
            return result;
        }

        // 更新为"已拒绝"
        orderMapper.updateRefundStatus(orderId, 3);

        RefundLog log = refundLogMapper.findByOrderId(orderId);
        if (log != null) {
            refundLogMapper.updateStatus(log.getId(), 3, adminRemark);
        }

        result.put("success", true);
        result.put("message", "退货申请已拒绝");
        result.put("orderSn", order.getOrderSn());
        return result;
    }

    /**
     * 完成退款（退货收到货后确认退款）
     * 这一步将 order.status 改为 5(已退款)，refund_status 改为 4
     */
    @Override
    @Transactional
    public Map<String, Object> completeRefund(Long orderId) {
        Map<String, Object> result = new LinkedHashMap<>();
        Order order = orderMapper.lookById(orderId);

        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        // 只有已同意退货中(2)的才能完成退款
        if (order.getRefundStatus() == null || order.getRefundStatus() != 2) {
            result.put("success", false);
            result.put("message", "订单退货状态不允许直接退款（需先审核通过）");
            return result;
        }

        // 执行退款：order.status→5, refund_status→4
        int rows = orderMapper.refundOrder(orderId);
        if (rows <= 0) {
            result.put("success", false);
            result.put("message", "退款失败");
            return result;
        }

        RefundLog log = refundLogMapper.findByOrderId(orderId);
        if (log != null) {
            refundLogMapper.updateStatus(log.getId(), 4, "退款完成");
        }

        result.put("success", true);
        result.put("message", "退款已完成");
        result.put("orderSn", order.getOrderSn());
        result.put("refundAmount", order.getTotalAmount());
        return result;
    }

    /**
     * Agent 一键退货（申请+审核+退款 一步到位，用于演示）
     */
    @Override
    @Transactional
    public Map<String, Object> oneClickRefund(Long orderId, Long userId, String reason) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 1. 检查资格
        Map<String, Object> eligibility = checkEligibility(orderId);
        if (!(Boolean) eligibility.get("eligible")) {
            result.put("success", false);
            result.put("message", eligibility.get("reason"));
            return result;
        }

        // 2. 申请退货
        Map<String, Object> applyResult = applyForReturn(orderId, userId, reason);
        if (!(Boolean) applyResult.get("success")) {
            return applyResult;
        }

        // 3. 自动审核通过
        approveReturn(orderId, "Agent自动审批");

        // 4. 完成退款
        Map<String, Object> completeResult = completeRefund(orderId);
        if (!(Boolean) completeResult.get("success")) {
            result.put("success", false);
            result.put("message", "退货审核通过但退款失败: " + completeResult.get("message"));
            return result;
        }

        result.put("success", true);
        result.put("message", "退货退款已完成");
        result.put("orderSn", eligibility.get("orderSn"));
        result.put("refundAmount", eligibility.get("totalAmount"));
        return result;
    }

    /**
     * 查询退货记录
     */
    @Override
    public Map<String, Object> getRefundInfo(Long orderId) {
        Map<String, Object> result = new LinkedHashMap<>();
        Order order = orderMapper.lookById(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        RefundLog log = refundLogMapper.findByOrderId(orderId);
        result.put("success", true);
        result.put("orderId", order.getId());
        result.put("orderSn", order.getOrderSn());
        result.put("orderStatus", statusText(order.getStatus()));
        result.put("refundStatus", order.getRefundStatus());
        result.put("refundStatusText", refundStatusText(order.getRefundStatus()));
        result.put("totalAmount", order.getTotalAmount());
        if (log != null) {
            result.put("reason", log.getReason());
            result.put("adminRemark", log.getAdminRemark());
            result.put("applyTime", log.getCreateTime());
        }
        return result;
    }

    private String statusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待付款";
            case 1 -> "待发货";
            case 2 -> "运输中";
            case 3 -> "已到货";
            case 4 -> "退货中";
            case 5 -> "已退款";
            default -> "未知";
        };
    }

    private String refundStatusText(Integer status) {
        if (status == null || status == 0) return "无退货";
        return switch (status) {
            case 1 -> "待审核";
            case 2 -> "已同意退货中";
            case 3 -> "已拒绝";
            case 4 -> "已退款";
            default -> "未知";
        };
    }
}
