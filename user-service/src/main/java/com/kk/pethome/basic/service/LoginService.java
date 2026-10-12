package com.kk.pethome.basic.service;

import com.kk.pethome.basic.dto.LoginDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface LoginService {
    Map<String, Object> accountLogin(LoginDto loginDto);
}
