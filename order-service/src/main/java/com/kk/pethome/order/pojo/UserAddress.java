package com.kk.pethome.order.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress extends BasicPojo {
    private Long userId;
    private String recipient;
    private String phone;
    private String address;
    private Boolean isDefault;
    private Date createTime;
}
