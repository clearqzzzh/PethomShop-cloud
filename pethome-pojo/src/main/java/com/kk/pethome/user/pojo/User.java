package com.kk.pethome.user.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BasicPojo {

    private String username;
    private String phone;
    private String email;
    private String salt;
    private String password;
    private Integer state;
    private Integer age;
    private Date createtime;
    private String headImg;
    private Long logininfoId;

}
