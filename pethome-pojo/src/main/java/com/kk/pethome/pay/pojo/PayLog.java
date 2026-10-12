package com.kk.pethome.pay.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayLog extends BasicPojo {
    private Long orderId;
    private String outTradeNo;
    private BigDecimal amount;
    private Integer status;     // 0-待支付 1-支付成功 2-支付失败
    private Date payTime;
    private Date createTime;
}
