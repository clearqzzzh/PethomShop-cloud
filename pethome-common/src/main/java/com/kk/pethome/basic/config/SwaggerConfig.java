package com.kk.pethome.basic.config;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // 配置接口文档的基本信息
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("宠物之家") // 对应原title
                        .description("宠物之家接口文档说明") // 对应原description
                        .contact(new Contact() // 对应原contact
                                .name("pethome")
                                .email("pethome@cjc.cn"))
                        .version("1.0")); // 对应原version
    }

    // 配置接口分组（对应原apis(RequestHandlerSelectors.basePackage)，指定扫描的包路径）
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("pethome") // 分组名称
                .pathsToMatch("/**") // 匹配所有路径
                .packagesToScan("com.kk.pethome.org.controller") // 对应原basePackage，指定扫描的controller包
                .build();
    }
}

