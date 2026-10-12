package com.kk.pethome.admin.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 独立管理员实体（与 t_user 客户表完全分离）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin extends BasicPojo {
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

    private String phone;
    private String email;
    private String avatar;
    private String role;      // super_admin / admin / operator
    private Integer state;    // 1正常 0禁用
    private Date createTime;
    private Date updateTime;
}
