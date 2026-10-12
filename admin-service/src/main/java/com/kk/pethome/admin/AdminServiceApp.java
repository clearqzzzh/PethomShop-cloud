package com.kk.pethome.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.kk.pethome")
@EnableDiscoveryClient
@MapperScan({"com.kk.pethome.org.mapper","com.kk.pethome.system.mapper","com.kk.pethome.user.mapper","com.kk.pethome.admin.mapper"})
public class AdminServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApp.class, args);
    }
}
