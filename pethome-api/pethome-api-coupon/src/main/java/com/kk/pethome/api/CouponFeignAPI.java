package com.kk.pethome.api;

import com.kk.pethome.basic.result.jsonresult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "coupon-service", path = "/api/coupon", fallback = CouponFeignFallback.class)
public interface CouponFeignAPI {

    /** 验券（下单前校验+预占锁定） */
    @PostMapping("/check")
    jsonresult checkAndReserve(@RequestBody Map<String, Object> body);

    /** 核销优惠券（支付成功后调用） */
    @PostMapping("/use")
    jsonresult useCoupon(@RequestBody Map<String, Object> body);

    /** 释放优惠券（订单取消/超时） */
    @PostMapping("/release")
    jsonresult releaseCoupon(@RequestBody Map<String, Object> body);

    /** 我的优惠券列表 */
    @GetMapping("/my")
    jsonresult myCoupons(@RequestParam Long userId);
}
