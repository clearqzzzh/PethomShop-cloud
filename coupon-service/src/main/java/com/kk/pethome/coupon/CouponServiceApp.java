package com.kk.pethome.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.kk.pethome")
@EnableDiscoveryClient
@MapperScan("com.kk.pethome.coupon.mapper")
public class CouponServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CouponServiceApp.class, args);
    }
}
