package com.kk.pethome.coupon.mapper;

import com.kk.pethome.coupon.pojo.CouponUsageLog;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CouponUsageLogMapper {
    @Insert("INSERT INTO pethome_order.coupon_usage_log(coupon_id,user_id,event_type,order_id,amount_deducted,remark) VALUES(#{couponId},#{userId},#{eventType},#{orderId},#{amountDeducted},#{remark})")
    int insert(CouponUsageLog log);
}
