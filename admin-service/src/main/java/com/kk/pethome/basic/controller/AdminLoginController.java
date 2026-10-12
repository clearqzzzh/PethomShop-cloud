package com.kk.pethome.basic.controller;

import com.kk.pethome.basic.dto.LoginDto;
import com.kk.pethome.basic.jwt.JwtUtils;
import com.kk.pethome.basic.jwt.PayloadDate;
import com.kk.pethome.basic.jwt.RsaUtils;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.org.mapper.EmployeeMapper;
import com.kk.pethome.org.pojo.Employee;
import com.kk.pethome.system.mapper.MenuMapper;
import com.kk.pethome.system.mapper.PermissionMapper;
import com.kk.pethome.system.pojo.Menu;
import com.kk.pethome.user.mapper.LoginInfoMapper;
import com.kk.pethome.user.pojo.loginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端登录控制器
 */
@RestController
@RequestMapping("/api/admin/login")
public class AdminLoginController {

    @Autowired
    private LoginInfoMapper loginInfoMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @PostMapping("/accountLogin")
    public jsonresult login(@RequestBody LoginDto loginDto) {
        // 1. 根据用户名和类型查询登录信息
        loginInfo info = loginInfoMapper.selectByUsername(loginDto.getUsername(), loginDto.getType());
        if (info == null) {
            return jsonresult.fail().setMessage("用户不存在");
        }

        // 2. 校验密码
        String inputPwd = DigestUtils.md5DigestAsHex((info.getSalt() + loginDto.getPassword()).getBytes());
        if (!inputPwd.equals(info.getPassword())) {
            return jsonresult.fail().setMessage("密码错误");
        }

        // 3. 检查账号是否禁用
        if (info.getDisable() != null && info.getDisable() == 1) {
            return jsonresult.fail().setMessage("账号已被禁用");
        }

        // 4. 构建JWT载荷
        PayloadDate payloadDate = new PayloadDate();
        payloadDate.setLogininfo(info);

        // 5. 如果是员工(type=0)，通过employee关联查询权限和菜单
        if (loginDto.getType() != null && loginDto.getType() == 0) {
            Employee employee = employeeMapper.queryByLoginInfoId(info.getId());
            if (employee != null) {
                List<String> permissions = permissionMapper.lookByEmployeeId(employee.getId());
                payloadDate.setPermission(permissions);

                List<Menu> menus = menuMapper.queryByEmpId(employee.getId());
                payloadDate.setMenu(menus);
            }
        }

        // 6. 生成token
        try {
            java.net.URL priUrl = AdminLoginController.class.getClassLoader().getResource("hrm_auth_rsa");
            if (priUrl == null) {
                return jsonresult.fail().setMessage("私钥文件未找到");
            }
            PrivateKey privateKey = RsaUtils.getPrivateKey(priUrl.getFile());
            String token = JwtUtils.generateTokenExpireInSeconds(payloadDate, privateKey, 7 * 24 * 60 * 60);

            // 7. 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            // 清除敏感信息
            info.setPassword(null);
            info.setSalt(null);
            result.put("loginInfo", info);
            result.put("permission", payloadDate.getPermission());
            result.put("menu", payloadDate.getMenu());

            return jsonresult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("token生成失败");
        }
    }
}
