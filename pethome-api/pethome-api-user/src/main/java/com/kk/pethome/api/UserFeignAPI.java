package com.kk.pethome.api;

import com.kk.pethome.basic.dto.LoginDto;
import com.kk.pethome.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", path = "/api/user")
public interface UserFeignAPI {

    @PostMapping("/login")
    String login(@RequestBody LoginDto dto);

    @GetMapping("/info/{id}")
    User getUserById(@PathVariable Long id);

    @PostMapping("/register")
    void register(@RequestBody User user);
}
