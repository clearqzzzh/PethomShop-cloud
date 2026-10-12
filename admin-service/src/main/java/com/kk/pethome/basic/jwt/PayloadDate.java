package com.kk.pethome.basic.jwt;

import com.kk.pethome.system.pojo.Menu;
import com.kk.pethome.user.pojo.loginInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * JWT载荷数据（与user-service保持一致）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayloadDate {
    private loginInfo logininfo;
    private List<String> permission;
    private List<Menu> menu;
}
