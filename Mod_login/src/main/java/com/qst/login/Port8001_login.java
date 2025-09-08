package com.qst.login;

import com.qst.domain.config.WebMvcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Import;

@Import(value = WebMvcConfig.class)
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.qst.login.mapper")
public class Port8001_login {
    public static void main(String[] args) {
        SpringApplication.run(Port8001_login.class,args);
    }
}
