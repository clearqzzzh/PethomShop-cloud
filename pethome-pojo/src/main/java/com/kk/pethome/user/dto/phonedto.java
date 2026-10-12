package com.kk.pethome.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class phonedto {
    private String phone;
    private String password;
    private String smsCode;
    private String type;
    private String repassword;
}
