package com.kk.pethome.user.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class loginInfo extends BasicPojo {
    private String username;
    private String phone;
    private String email;
    private String salt;
    private String password;
    private Integer type;
    private Integer disable;

}
