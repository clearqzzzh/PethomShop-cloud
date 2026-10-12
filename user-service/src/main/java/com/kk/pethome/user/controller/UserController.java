package com.kk.pethome.user.controller;

import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.user.mapper.LoginInfoMapper;
import com.kk.pethome.user.mapper.UserMapper;
import com.kk.pethome.user.pojo.User;
import com.kk.pethome.user.pojo.loginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserMapper userMapper;

    @Autowired
    LoginInfoMapper loginInfoMapper;

    /** 按手机号查用户（Agent用） */
    @GetMapping("/lookup")
    public jsonresult lookupByPhone(@RequestParam String phone) {
        User user = userMapper.getByPhone(phone);
        if (user == null) return jsonresult.fail().setMessage("未找到用户");
        return jsonresult.success(mapUser(user));
    }

    /** 按ID查用户（Agent用，已登录免手机号） */
    @GetMapping("/{id}")
    public jsonresult lookupById(@PathVariable Long id) {
        User user = userMapper.lookById(id);
        if (user == null) return jsonresult.fail().setMessage("未找到用户");
        return jsonresult.success(mapUser(user));
    }

    /** Agent 场景：用户注册 */
    @PostMapping("/register")
    public jsonresult register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String phone = body.get("phone");
        String password = body.get("password");
        if (username == null || phone == null || password == null) {
            return jsonresult.fail().setMessage("用户名、手机号、密码不能为空");
        }
        // 检查手机号是否已注册
        User existing = userMapper.getByPhone(phone);
        if (existing != null) {
            return jsonresult.fail().setMessage("该手机号已注册");
        }
        // 生成盐值 + MD5加密
        String salt = UUID.randomUUID().toString();
        String hashedPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes());

        // 1. 插入 t_logininfo
        loginInfo li = new loginInfo();
        li.setUsername(username);
        li.setPhone(phone);
        li.setPassword(hashedPassword);
        li.setSalt(salt);
        li.setType(1);   // 普通用户
        li.setDisable(0); // 正常状态
        loginInfoMapper.Add(li);
        Long loginInfoId = li.getId();

        // 2. 插入 t_user
        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        user.setLogininfoId(loginInfoId);
        user.setState(1);
        userMapper.Add(user);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", user.getId());
        result.put("username", username);
        result.put("phone", phone);
        result.put("points", 100); // 注册赠送积分
        return jsonresult.success(result);
    }

    private Map<String, Object> mapUser(User user) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", user.getId());
        m.put("username", user.getUsername());
        m.put("phone", user.getPhone());
        return m;
    }

    // ═══════ 管理端用户管理 ═══════

    /** 管理端：查询所有用户列表 */
    @GetMapping("/admin/list")
    public jsonresult adminListUsers(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<User> all = userMapper.lookAll();
        // 脱敏：不返回密码/盐值
        all.forEach(u -> { u.setPassword(null); u.setSalt(null); });
        // 按创建时间倒序
        all.sort((a, b) -> {
            if (b.getCreatetime() == null || a.getCreatetime() == null) return 0;
            return b.getCreatetime().compareTo(a.getCreatetime());
        });
        int total = all.size();
        int from = (currentPage - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        List<User> page = (from >= total) ? List.of() : all.subList(from, to);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", page);
        result.put("total", total);
        return jsonresult.success(result);
    }

    /** 管理端：用户统计 */
    @GetMapping("/admin/stats")
    public jsonresult adminUserStats() {
        List<User> all = userMapper.lookAll();
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", all.size());

        // 最近7天每日注册数
        Map<String, Integer> dailyRegistrations = new LinkedHashMap<>();
        java.text.SimpleDateFormat dayFmt = new java.text.SimpleDateFormat("MM-dd");
        Calendar cal = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            dailyRegistrations.put(dayFmt.format(cal.getTime()), 0);
        }
        for (User u : all) {
            if (u.getCreatetime() != null) {
                String day = dayFmt.format(u.getCreatetime());
                if (dailyRegistrations.containsKey(day)) {
                    dailyRegistrations.merge(day, 1, Integer::sum);
                }
            }
        }
        stats.put("dailyRegistrations", dailyRegistrations);
        return jsonresult.success(stats);
    }
}
