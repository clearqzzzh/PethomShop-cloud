package com.kk.pethome.coupon.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponUsageLog {
    private Long id; private Long userId; private Long couponId;
    private String eventType; private Long orderId;
    private BigDecimal amountDeducted; private String remark;
}
