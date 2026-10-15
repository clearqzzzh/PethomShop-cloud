package com.kk.pethome.api;

import com.kk.pethome.basic.result.jsonresult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OrderFeignAPI 降级处理
 * 当下游 order-service 不可用时返回兜底数据
 */
@Component
public class OrderFeignFallback implements OrderFeignAPI {

    @Override
    public jsonresult createDirectOrder(Map<String, Object> orderData) {
        return jsonresult.fail("订单服务暂时不可用，请稍后再试");
    }

    @Override
    public jsonresult getOrderById(Long id) {
        return jsonresult.fail("订单服务暂时不可用，请稍后再试");
    }

    @Override
    public jsonresult getUserOrders(Long userId) {
        return jsonresult.fail("订单服务暂时不可用，请稍后再试");
    }

    @Override
    public jsonresult payOrder(Long id) {
        return jsonresult.fail("订单服务暂时不可用，支付确认失败");
    }

    @Override
    public jsonresult getByOrderSn(String orderSn) {
        return jsonresult.fail("订单服务暂时不可用，请稍后再试");
    }
}
