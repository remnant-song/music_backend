package com.qst.singer;

import com.qst.domain.config.MybatisPlusPageConfig;
import com.qst.domain.config.WebMvcConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@Import(value = {WebMvcConfig.class, MybatisPlusPageConfig.class})
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.qst.singer.mapper")
public class Port8004_singer {
    public static void main(String[] args) {
        SpringApplication.run(Port8004_singer.class,args);
    }
}