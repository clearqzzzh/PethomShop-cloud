package com.kk.pethome.coupon.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class UserCoupon {
    private Long id; private Long userId; private Long templateId;
    private Long seckillId; private Integer status;
    private Date obtainTime; private Date useTime;
    private Date expireTime; private Long orderId;
}
