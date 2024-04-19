package com.github.config;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     CorePoolSize: 4 - CPU 코어 수와 동일하게 설정하여 항상 실행 준비가 되어 있는 스레드 수를 유지
     MaxPoolSize: 10 - 최대 동시 실행 스레드 수를 CPU 코어 수의 2-3배로 설정하여 고부하 상황에서도 충분한 처리 능력을 확보
     QueueCapacity: 100 - 작업 큐의 용량으로, CorePoolSize를 초과하는 작업은 큐에 보관. 동시에 많은 요청이 들어왔을 때 일정 수의 요청을 대기시킬 수 있도록
     */
    @Override
    public Executor getAsyncExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("MyAsyncExecutor-");
        executor.initialize();
        return executor;
    }

}
