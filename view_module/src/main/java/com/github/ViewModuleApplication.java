package com.github;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ViewModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ViewModuleApplication.class, args);
    }
}