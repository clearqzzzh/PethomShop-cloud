package com.kk.pethome.coupon.service;

import com.kk.pethome.coupon.pojo.CouponSeckill;
import com.kk.pethome.coupon.pojo.CouponTemplate;
import com.kk.pethome.coupon.pojo.UserCoupon;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CouponService {

    int seckill(long activityId, long userId);

    Map<String, Object> checkAndReserve(Long userId, Long couponId, BigDecimal orderAmount);

    void useCoupon(Long couponId, Long userId, Long orderId, BigDecimal actualDiscount);

    void releaseCoupon(Long couponId, Long userId);

    void preloadStock(long activityId, int stock);

    List<UserCoupon> myCoupons(Long userId);

    List<CouponTemplate> listTemplates();

    CouponTemplate createTemplate(CouponTemplate tpl);

    void deleteTemplate(Long id);

    List<CouponSeckill> listActiveSeckills();

    CouponSeckill createSeckillAndPreload(Long templateId, int seckillStock, int perUserLimit, Date startTime, Date endTime);
}
