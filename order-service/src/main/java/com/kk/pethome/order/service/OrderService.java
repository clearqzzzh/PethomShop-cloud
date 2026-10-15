package com.kk.pethome.order.service;

import com.kk.pethome.order.pojo.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, List<Long> cartItemIds,
                      String recipient, String phone, String address,
                      Long couponId);
    List<Order> getOrders(Long userId);
    Order getOrder(Long orderId);
    List<Order> getRecentOrders(Long userId, int limit);
}
