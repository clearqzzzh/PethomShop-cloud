package com.kk.pethome.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "pay-service", path = "/api/pay")
public interface PayFeignAPI {

    @PostMapping("/create")
    String createPayOrder(@RequestParam String orderId,
                           @RequestParam BigDecimal amount,
                           @RequestParam(defaultValue = "宠物商城订单") String subject);
}
