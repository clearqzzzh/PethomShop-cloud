package com.kk.pethome.order.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BasicPojo {
    /** 订单号（业务唯一标识，如 PH202609171430220001） */
    private String orderSn;
    private Long userId;
    private String recipient;
    private String phone;
    private String address;
    private BigDecimal totalAmount;
    /** 0=待付款 1=待发货 2=运输中 3=已到货 4=退货中 5=已退款 */
    private Integer status;
    private Date createTime;
    private Date payTime;
    private Long couponId;
    private BigDecimal discountAmount;
    /** 物流单号 */
    private String trackingNo;
    /** 发货时间 */
    private Date shipTime;
    /** 签收时间 */
    private Date receiveTime;
    /** 退货状态: 0无退货 1待审核 2已同意退货中 3已拒绝 4已退款 */
    private Integer refundStatus;
    private List<OrderItem> items;
}
