package com.qst.admin;

import com.qst.domain.config.MybatisPlusPageConfig;
import com.qst.domain.config.WebMvcConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@Import(value = {WebMvcConfig.class, MybatisPlusPageConfig.class})
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.qst.admin.mapper")
public class Port8003_admin {
    public static void main(String[] args) {
        SpringApplication.run(Port8003_admin.class,args);
    }
}