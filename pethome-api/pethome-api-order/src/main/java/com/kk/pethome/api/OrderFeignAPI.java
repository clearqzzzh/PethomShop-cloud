package com.kk.pethome.api;

import com.kk.pethome.basic.result.jsonresult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "order-service", path = "/api/order", fallback = OrderFeignFallback.class)
public interface OrderFeignAPI {

    @PostMapping("/direct")
    jsonresult createDirectOrder(@RequestBody Map<String, Object> orderData);

    @GetMapping("/{id}")
    jsonresult getOrderById(@PathVariable Long id);

    @GetMapping
    jsonresult getUserOrders(@RequestParam Long userId);

    /** 支付确认——更新订单为已支付 */
    @PutMapping("/{id}/pay")
    jsonresult payOrder(@PathVariable Long id);

    /** 根据订单号查询订单 */
    @GetMapping("/sn/{orderSn}")
    jsonresult getByOrderSn(@PathVariable String orderSn);
}
