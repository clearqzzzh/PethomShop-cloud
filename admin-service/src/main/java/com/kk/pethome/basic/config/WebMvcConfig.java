package com.kk.pethome.basic.config;

import com.kk.pethome.basic.jwt.JwtInterceptor;
import com.kk.pethome.basic.jwt.RsaUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.PublicKey;

/**
 * WebMvc配置：注册JWT拦截器 + CORS跨域
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private PublicKey publicKey;

    public WebMvcConfig() throws Exception {
        // 从classpath读取RSA公钥
        java.net.URL pubUrl = WebMvcConfig.class.getClassLoader().getResource("hrm_auth_rsa.pub");
        if (pubUrl == null) throw new RuntimeException("classpath下未找到 hrm_auth_rsa.pub 公钥文件");
        this.publicKey = RsaUtils.getPublicKey(pubUrl.getFile());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor(publicKey))
                .addPathPatterns("/api/admin/**")       // 拦截所有admin接口
                .excludePathPatterns(
                        "/api/admin/login/**",       // 放行员工登录
                        "/api/admin/auth/login",     // 放行管理员登录
                        "/api/admin/auth/register"   // 放行管理员注册
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
