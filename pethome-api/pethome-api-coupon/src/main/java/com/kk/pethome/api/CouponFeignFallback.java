package com.kk.pethome.api;

import com.kk.pethome.basic.result.jsonresult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * CouponFeignAPI 降级处理
 * 当下游 coupon-service 不可用时返回兜底数据
 */
@Component
public class CouponFeignFallback implements CouponFeignAPI {

    @Override
    public jsonresult checkAndReserve(Map<String, Object> body) {
        return jsonresult.fail("优惠券服务暂时不可用，请稍后再试");
    }

    @Override
    public jsonresult useCoupon(Map<String, Object> body) {
        return jsonresult.fail("优惠券服务暂时不可用");
    }

    @Override
    public jsonresult releaseCoupon(Map<String, Object> body) {
        return jsonresult.fail("优惠券服务暂时不可用");
    }

    @Override
    public jsonresult myCoupons(Long userId) {
        return jsonresult.fail("优惠券服务暂时不可用，请稍后再试");
    }
}
