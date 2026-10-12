package com.kk.pethome.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class emaildto {
        private String email;
        private String password;
        private String repassword;
}
