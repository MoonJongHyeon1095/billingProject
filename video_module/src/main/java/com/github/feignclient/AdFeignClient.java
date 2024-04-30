package com.github.feignclient;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "adFeignClient", url = "http://localhost:8083")
public interface AdFeignClient {

    @Retry(name = "adFeignClient", fallbackMethod = "fallbackRetryCreateAdDetail")
    @CircuitBreaker(name = "adFeignClient", fallbackMethod = "fallbackCreateAdDetail")
    @PostMapping("/v1/advertisement/{videoId}/{adViewCount}")
    void createAdDetail(
            @PathVariable("videoId") int videoId,
            @PathVariable("adViewCount") int adViewCount
    );

    default void fallbackCreateAdDetail(int videoId, int adViewCount, Throwable t) {
        // 여기에 fallback 로직 구현

    }


    @GetMapping("/v1/advertisement/error/case1")
    void case1();

    default void fallbackCircuitBreakerCase1(Throwable t) {
        System.out.println("fallbackCircuitBreakerCase1");

    }

    default void fallbackRetryErrorCase(Throwable t) {
        System.out.println("retry fallback");
    }
}
