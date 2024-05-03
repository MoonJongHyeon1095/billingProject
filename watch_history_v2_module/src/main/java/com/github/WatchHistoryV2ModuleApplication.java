package com.github;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WatchHistoryV2ModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(WatchHistoryV2ModuleApplication.class, args);
    }
}