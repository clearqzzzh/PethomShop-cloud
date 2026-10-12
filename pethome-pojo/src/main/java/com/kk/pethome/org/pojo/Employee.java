package com.kk.pethome.org.pojo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee extends BasicPojo {
    private String username;
    private String password;
    //确认密码
    @JsonProperty("confirmPassword")
    private String confirmPassword;
    private String phone;
    private String email;
    private Integer state;
    private Long dept_id;
    private Long logininfoId;
    private Long shopId;
    private String salt;
    private Long departmentId;
    private int age;
    private Department department;
}
