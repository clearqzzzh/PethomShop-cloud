package com.kk.pethome.coupon.mapper;

import com.kk.pethome.coupon.pojo.UserCoupon;
import org.apache.ibatis.annotations.*;
import com.kk.pethome.coupon.pojo.UserCoupon;
import java.util.*;

@Mapper
public interface UserCouponMapper {
    @Select("SELECT * FROM pethome_member.user_coupon WHERE id=#{id}")
    UserCoupon getById(Long id);

    @Select("SELECT * FROM pethome_member.user_coupon WHERE user_id=#{userId} AND status=0 AND expire_time>NOW()")
    List<UserCoupon> findByUser(Long userId);

    @Update("UPDATE pethome_member.user_coupon SET status=3 WHERE id=#{id} AND status=0")
    int lockCoupon(Long id);

    @Update("UPDATE pethome_member.user_coupon SET status=1, use_time=#{useTime}, order_id=#{orderId} WHERE id=#{id}")
    int useCoupon(@Param("id") Long id, @Param("orderId") Long orderId, @Param("useTime") Date useTime);

    @Update("UPDATE pethome_member.user_coupon SET status=0 WHERE id=#{id} AND status=3")
    int releaseCoupon(Long id);

    @Update("UPDATE pethome_member.user_coupon SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") int status);

    @Insert("INSERT INTO pethome_member.user_coupon(user_id,template_id,seckill_id,status,expire_time) VALUES(#{userId},#{templateId},#{seckillId},#{status},#{expireTime})")
    @Options(useGeneratedKeys=true,keyProperty="id")
    int insert(UserCoupon uc);
}
