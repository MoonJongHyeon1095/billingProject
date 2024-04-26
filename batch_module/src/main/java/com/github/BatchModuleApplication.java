package com.github;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
@MapperScan("com.github.mapper")
public class BatchModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchModuleApplication.class, args);
    }
}