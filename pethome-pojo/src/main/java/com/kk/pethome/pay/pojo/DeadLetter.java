package com.kk.pethome.pay.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeadLetter extends BasicPojo {
    private String topic;
    private String messageBody;
    private String failReason;
    private Date createTime;
}
