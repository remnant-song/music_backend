package com.qst.setting;

import com.qst.domain.config.WebMvcConfig;
//import com.qst.domain.huawei.HuaweiStorageUploadService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(WebMvcConfig.class)
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.qst.setting.mapper")
public class Port8005_setting {

    public static void main(String[] args) {
        SpringApplication.run(Port8005_setting.class, args);
    }

}