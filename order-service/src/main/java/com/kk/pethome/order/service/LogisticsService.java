package com.kk.pethome.order.service;

import com.kk.pethome.order.pojo.Order;

import java.util.List;
import java.util.Map;

public interface LogisticsService {
    List<Map<String, Object>> getLogisticsTimeline(Order order);
    String generateTrackingNo(Long orderId);
    String getCourierName(String trackingNo);
}
