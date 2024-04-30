package com.github.service;

import com.github.feignclient.AdFeignClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ResilienceService {
    private final AdFeignClient adFeignClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    public ResilienceService(AdFeignClient adFeignClient, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.adFeignClient = adFeignClient;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    public void createAdDetail(int videoId, int adViewCount) {
        // 이 메소드는 Feign 클라이언트가 실제로 호출되는 부분입니다.
        // 실제 구현에서는 여기에 HTTP 요청을 보내는 로직이 포함됩니다.
        adFeignClient.createAdDetail(videoId, adViewCount);
        System.out.println("Creating ad detail for videoId: " + videoId + ", adViewCount: " + adViewCount);
    }

    //@Retry(name = "adFeignClient", fallbackMethod = "fallbackRetryErrorCase")
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "adFeignClient", fallbackMethod = "fallbackCircuitBreakerCase1")
    public void case1() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("adFeignClient");
        adFeignClient.case1();
        log.info("Executing case1 method");
        log.info("CircuitBreaker state for adFeignClient: {}", circuitBreaker.getState());
    }


}
