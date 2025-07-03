package com.qst.music;

import com.qst.domain.config.WebMvcConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@Import(value = WebMvcConfig.class)
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.qst.music.mapper")
public class Port8002_music {
    public static void main(String[] args) {
        SpringApplication.run(Port8002_music.class,args);
    }
}