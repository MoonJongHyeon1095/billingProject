package com.github.config.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * ExecutorService
 - 기본 개념: Java 표준 라이브러리의 스레드 관리 인터페이스. 다양한 종류의 스레드 풀을 생성.
 * 유형: FixedThreadPool, CachedThreadPool, ScheduledThreadPool, WorkStealingPool, VirtualThreadPerTaskExecutor 등 여러 유형
 * 주요 기능: 스레드 풀 관리, 작업 제출 및 실행, 스레드 종료 및 재사용을 제어.
 * 스레드 풀의 크기를 유연하게 설정하고 작업량에 따라 스레드 수를 조정.
 * 예시 사용 사례: 일반적인 병렬 작업, 작업 스케줄링, 작업 스레드 관리, Spring 배치 작업 등.
 *
 * ThreadPoolTaskExecutor: 플랫폼 스레드에 기반
 - Spring 프레임워크에서 제공하는 스레드 풀 실행기. ExecutorService를 기반으로 하고, Spring 환경에 적합한 추가 기능을 제공.
 - 유형: ThreadPoolTaskExecutor는 ThreadPoolExecutor를 내부적으로 사용
 - 주요 기능: 스레드 풀의 기본 크기, 최대 크기, 큐 크기, 스레드 이름 접두사 등을 설정. Spring의 이벤트 드리븐 환경과 잘 통합.
 -  예시 사용 사례: Spring 애플리케이션의 병렬 작업, 비동기 작업, 웹 요청 처리, Spring 배치 작업 등.
 *
 */
@Slf4j
@Configuration
public class ExecutorServiceConfig {
    @Bean(name = "taskPool")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setThreadNamePrefix("partition-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnThreading(Threading.VIRTUAL)
    public ExecutorService virtualThreadExecutor(){
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        executor.submit(() -> log.info("가상 스레드 생성"));
        return executor;
    }

    @Bean
    @ConditionalOnThreading(Threading.PLATFORM)
    public ExecutorService platformThreadExecutor(){
        return Executors.newCachedThreadPool();
    }

}