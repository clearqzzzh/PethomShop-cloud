package com.kk.pethome.order.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundLog {
    private Long id;
    private Long orderId;
    private String orderSn;
    private Long userId;
    /** 退款原因 */
    private String reason;
    /** 退款金额 */
    private BigDecimal refundAmount;
    /** 1待审核 2已同意 3已拒绝 4已退款 */
    private Integer status;
    /** 管理员备注 */
    private String adminRemark;
    private Date createTime;
    private Date updateTime;
}
