package com.kk.pethome.basic.controller;

import com.kk.pethome.basic.exception.BusinessException;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.basic.dto.LoginDto;
import com.kk.pethome.basic.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private LoginService loginService;
    @PostMapping("/accountLogin")
    public jsonresult accountLogin(@RequestBody LoginDto loginDto){
        try {
            //token Login对象
           Map<String,Object> maps =  loginService.accountLogin(loginDto);
           return new jsonresult(maps);
        } catch (BusinessException e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("登录失败");
        }
    }
}
