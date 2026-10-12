package com.kk.pethome.admin.controller;

import com.kk.pethome.admin.pojo.Admin;
import com.kk.pethome.admin.service.AdminService;
import com.kk.pethome.basic.jwt.JwtUtils;
import com.kk.pethome.basic.jwt.PayloadDate;
import com.kk.pethome.basic.jwt.RsaUtils;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.user.pojo.loginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.util.*;

/**
 * 管理员独立认证控制器
 * 使用 t_admin 表，与 t_logininfo/t_user 客户体系完全分离
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录（独立于客户登录）
     * POST /api/admin/auth/login  { "username": "admin", "password": "admin123" }
     */
    @PostMapping("/login")
    public jsonresult login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return jsonresult.fail().setMessage("用户名和密码不能为空");
        }

        // 1. 查找管理员
        Admin admin = adminService.findByUsername(username);
        if (admin == null) {
            return jsonresult.fail().setMessage("管理员账号不存在");
        }

        // 2. 校验密码：MD5(salt + password)
        String inputPwd = DigestUtils.md5DigestAsHex((admin.getSalt() + password).getBytes());
        if (!inputPwd.equals(admin.getPassword())) {
            return jsonresult.fail().setMessage("密码错误");
        }

        // 3. 检查状态
        if (admin.getState() != null && admin.getState() == 0) {
            return jsonresult.fail().setMessage("账号已被禁用");
        }

        // 4. 构建JWT（复用现有JWT基础设施）
        //    构造一个 loginInfo 对象放入 Payload，让 JwtInterceptor 能正常解析
        loginInfo fakeLoginInfo = new loginInfo();
        fakeLoginInfo.setId(admin.getId());
        fakeLoginInfo.setUsername(admin.getUsername());
        fakeLoginInfo.setPhone(admin.getPhone());
        fakeLoginInfo.setType(0);  // 管理员类型

        PayloadDate payload = new PayloadDate();
        payload.setLogininfo(fakeLoginInfo);
        payload.setPermission(List.of(
            "employee:add", "employee:edit", "employee:delete",
            "product:add", "product:edit", "product:delete",
            "order:ship", "order:refund",
            "user:view"
        ));

        try {
            java.net.URL priUrl = AdminAuthController.class.getClassLoader().getResource("hrm_auth_rsa");
            if (priUrl == null) {
                return jsonresult.fail().setMessage("私钥文件未找到");
            }
            PrivateKey privateKey = RsaUtils.getPrivateKey(priUrl.getFile());
            String token = JwtUtils.generateTokenExpireInSeconds(payload, privateKey, 7 * 24 * 60 * 60);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("token", token);
            result.put("admin", admin);
            result.put("permission", payload.getPermission());

            return jsonresult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("token生成失败: " + e.getMessage());
        }
    }

    /**
     * 管理员注册（仅超级管理员可调用，或首次初始化）
     * POST /api/admin/auth/register  { "username":"xxx", "password":"xxx", "phone":"xxx" }
     */
    @PostMapping("/register")
    public jsonresult register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String phone = body.get("phone");

        if (username == null || password == null) {
            return jsonresult.fail().setMessage("用户名和密码不能为空");
        }

        // 检查用户名是否已存在
        Admin existing = adminService.findByUsername(username);
        if (existing != null) {
            return jsonresult.fail().setMessage("用户名已存在");
        }

        // 生成盐值 + MD5
        String salt = UUID.randomUUID().toString();
        String hashedPwd = DigestUtils.md5DigestAsHex((salt + password).getBytes());

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(hashedPwd);
        admin.setSalt(salt);
        admin.setPhone(phone);
        admin.setRole("admin");
        admin.setState(1);

        adminService.add(admin);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", admin.getId());
        result.put("username", username);
        result.put("role", "admin");

        return jsonresult.success(result);
    }

    /**
     * 获取当前登录管理员信息
     * GET /api/admin/auth/me
     */
    @GetMapping("/me")
    public jsonresult me(jakarta.servlet.http.HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("currentUserId");
        if (adminId == null) {
            return jsonresult.fail().setMessage("未登录");
        }
        Admin admin = adminService.lookById(adminId);
        if (admin == null) {
            return jsonresult.fail().setMessage("管理员不存在");
        }
        return jsonresult.success(admin);
    }
}
