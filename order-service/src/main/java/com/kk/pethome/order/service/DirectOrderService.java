package com.kk.pethome.order.service;

import com.kk.pethome.order.pojo.Order;

import java.util.Map;

public interface DirectOrderService {
    Order createDirect(Map<String, Object> body);
}
