package com.kk.pethome.pay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.kk.pethome")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.kk.pethome.api")
@MapperScan("com.kk.pethome.pay.mapper")
public class PayServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(PayServiceApp.class, args);
    }
}
