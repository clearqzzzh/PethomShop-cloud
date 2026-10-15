package com.kk.pethome.order.service;

import java.util.Map;

public interface RefundService {
    Map<String, Object> checkEligibility(Long orderId);
    Map<String, Object> applyForReturn(Long orderId, Long userId, String reason);
    Map<String, Object> approveReturn(Long orderId, String adminRemark);
    Map<String, Object> rejectReturn(Long orderId, String adminRemark);
    Map<String, Object> completeRefund(Long orderId);
    Map<String, Object> oneClickRefund(Long orderId, Long userId, String reason);
    Map<String, Object> getRefundInfo(Long orderId);
}
