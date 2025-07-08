package com.qst.upload;

import com.qst.domain.config.WebMvcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Import(value = WebMvcConfig.class)
@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
public class port8007_upload {
    public static void main(String[] args) {
        SpringApplication.run(port8007_upload.class,args);
    }

}