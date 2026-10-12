package com.kk.pethome.org.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopAuditLog extends BasicPojo {
    private Integer state;
    private Long shopId;
    private Long auditId;
    private Date auditTime;
    private String note;

}
