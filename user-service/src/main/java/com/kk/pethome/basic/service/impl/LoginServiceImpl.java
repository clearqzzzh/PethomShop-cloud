package com.kk.pethome.basic.service.impl;

import com.aliyuncs.utils.StringUtils;
import com.kk.pethome.basic.dto.LoginDto;
import com.kk.pethome.basic.exception.BusinessException;
import com.kk.pethome.basic.jwt.JwtUtils;
import com.kk.pethome.basic.jwt.PayloadDate;
import com.kk.pethome.basic.jwt.RsaUtils;
import com.kk.pethome.basic.service.LoginService;
import com.kk.pethome.org.mapper.EmployeeMapper;
import com.kk.pethome.org.pojo.Employee;
import com.kk.pethome.system.mapper.MenuMapper;
import com.kk.pethome.system.mapper.PermissionMapper;
import com.kk.pethome.system.pojo.Menu;
import com.kk.pethome.user.mapper.LoginInfoMapper;
import com.kk.pethome.user.pojo.loginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private LoginInfoMapper loginInfoMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
     private PermissionMapper permissionMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Override
    public Map<String, Object> accountLogin(LoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        Integer type = loginDto.getType();
        //非空校验
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(type.toString())){
            throw new BusinessException("用户名、密码、登录类型不能为空");
        }
        //根据用户名查询登录信息
        loginInfo loginInfo = loginInfoMapper.selectByUsername(username,type);
        if(loginInfo == null){
            throw new BusinessException("用户名不存在");
        }
        //校验密码是否一致
        //把输入的密码加盐加密
        String encryptPassword = DigestUtils.md5DigestAsHex((loginInfo.getSalt() + password).getBytes());
        if(!encryptPassword.equals(loginInfo.getPassword())){
            throw new BusinessException("密码错误");
        }
/*        //生成一个随机的字符串作为token
        String token = UUID.randomUUID().toString();
        //把token存储到redis中
        redisTemplate.opsForValue().set(token, loginInfo,10, TimeUnit.DAYS);
        //返回token
        Map<String, Object> map = new HashMap<>();
        map.put("token",token);
        map.put("loginInfo",loginInfo);*/
        PayloadDate payloadDate = new PayloadDate();
        if(type == 0){
            //jwt: menu employee permission
            //获取登录人的permission
            Employee employee = employeeMapper.queryByLoginInfoId(loginInfo.getId());
            List<String> permission = permissionMapper.lookByEmployeeId(employee.getId());
            //获取登陆人的menu
            List<Menu> menu = menuMapper.queryByEmpId(employee.getId());
            //生成token
            payloadDate = new PayloadDate(loginInfo, permission, menu);;
        }
        PrivateKey privateKey = null;
        try {
            privateKey = RsaUtils.getPrivateKey(JwtUtils.class.getClassLoader().getResource("hrm_auth_rsa").getFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String token = JwtUtils.generateTokenExpireInSeconds(payloadDate , privateKey, 7*24*60*60);
        HashMap<String, Object> map = new HashMap<>();
        //脱敏
        loginInfo.setPassword(" ");
        loginInfo.setSalt(" ");
        map.put("token",token);
        map.put("loginInfo",loginInfo);
        map.put("permission",payloadDate.getPermission());
        map.put("menu",payloadDate.getMenu());
        return map;
    }

    public static void main(String[] args) {
        String salt =  UUID.randomUUID().toString();
        System.out.println(salt);
        String s =  DigestUtils.md5DigestAsHex((salt+"123").getBytes());
        System.out.println(s);
    }

}
