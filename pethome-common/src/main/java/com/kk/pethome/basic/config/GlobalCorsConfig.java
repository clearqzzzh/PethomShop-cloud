package com.kk.pethome.basic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://127.0.0.1:81", "http://localhost:81",
                        "http://127.0.0.1:8080", "http://localhost:8080",
                        "http://127.0.0.1:8082", "http://localhost:8082",
                        "http://127.0.0.1:8083", "http://localhost:8083",
                        "http://127.0.0.1:8085", "http://localhost:8085",
                        "http://127.0.0.1:8501", "http://localhost:8501"
                )
                .allowedMethods("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")  // 允许的请求方式
                .allowedHeaders("*")                // 允许的头信息
                .allowCredentials(true);            // 是否发送Cookie信息
    }
}