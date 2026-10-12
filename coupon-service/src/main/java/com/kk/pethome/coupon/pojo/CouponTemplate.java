package com.kk.pethome.coupon.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponTemplate {
    private Long id; private String name; private Integer type;
    private BigDecimal discountValue; private BigDecimal minOrderAmount;
    private Integer totalStock; private Integer issuedCount;
    private Date validStart; private Date validEnd;
    private Integer status;
}
