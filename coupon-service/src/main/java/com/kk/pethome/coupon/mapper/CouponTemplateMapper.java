package com.kk.pethome.coupon.mapper;

import com.kk.pethome.coupon.pojo.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CouponTemplateMapper {
    @Select("SELECT * FROM coupon_template WHERE id=#{id}")
    CouponTemplate getById(Long id);
    @Select("SELECT * FROM coupon_seckill WHERE id=#{id}")
    CouponSeckill getSeckillById(Long id);

    @Select("SELECT * FROM coupon_template ORDER BY id DESC")
    List<CouponTemplate> listAll();

    @Insert("INSERT INTO coupon_template(name,type,discount_value,min_order_amount,total_stock,issued_count,valid_start,valid_end,status) " +
            "VALUES(#{name},#{type},#{discountValue},#{minOrderAmount},#{totalStock},#{issuedCount},#{validStart},#{validEnd},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CouponTemplate tpl);

    @Delete("DELETE FROM coupon_template WHERE id=#{id}")
    int deleteById(Long id);

    @Update("UPDATE coupon_template SET issued_count=issued_count+1 WHERE id=#{id}")
    int incrIssued(Long id);

    /** 查询活跃秒杀活动（关联模板信息） */
    @Select("SELECT s.id as id, s.template_id as templateId, s.start_time as startTime, s.end_time as endTime, " +
            "s.seckill_stock as seckillStock, s.per_user_limit as perUserLimit, s.status, " +
            "t.name, t.type, t.discount_value as discountValue, t.min_order_amount as minOrderAmount, t.total_stock as totalStock, t.issued_count as issuedCount " +
            "FROM coupon_seckill s LEFT JOIN coupon_template t ON s.template_id=t.id " +
            "WHERE s.status=1 AND s.start_time<=NOW() AND s.end_time>=NOW() ORDER BY s.start_time ASC")
    List<CouponSeckill> listActiveSeckills();

    @Select("SELECT * FROM coupon_seckill WHERE template_id=#{templateId} AND status=1 AND end_time >= NOW() ORDER BY id DESC LIMIT 1")
    CouponSeckill findActiveSeckillByTemplate(Long templateId);

    @Insert("INSERT INTO coupon_seckill(template_id,start_time,end_time,seckill_stock,per_user_limit,status) " +
            "VALUES(#{templateId},#{startTime},#{endTime},#{seckillStock},#{perUserLimit},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSeckill(CouponSeckill seckill);
}
