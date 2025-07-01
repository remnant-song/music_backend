package com.qst.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MusicGateway {
    public static void main(String[] args) {
        SpringApplication.run(MusicGateway.class,args);
    }
}