package com.kk.pethome.coupon.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class CouponSeckill {
    private Long id; private Long templateId;
    private Date startTime; private Date endTime;
    private Integer seckillStock; private Integer perUserLimit;
    private Integer status;
    // 模板字段（listActiveSeckills 关联查询用）
    private String name;
    private Integer type;
    private java.math.BigDecimal discountValue;
    private java.math.BigDecimal minOrderAmount;
    private Integer totalStock;
    private Integer issuedCount;
}
