package com.github.service;

import com.github.feignclient.AdFeignClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import io.vavr.control.Try;
import java.util.function.Supplier;

@Slf4j
@Service
public class ResilienceService {
    private final AdFeignClient adFeignClient;
    private final CircuitBreaker circuitBreaker;

    public ResilienceService(AdFeignClient adFeignClient,  CircuitBreakerRegistry circuitBreakerRegistry) {
        this.adFeignClient = adFeignClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("adFeignClient");

    }

    public void createAdDetail(int videoId, int adViewCount) {
        // 이 메소드는 Feign 클라이언트가 실제로 호출되는 부분입니다.
        // 실제 구현에서는 여기에 HTTP 요청을 보내는 로직이 포함됩니다.
        adFeignClient.createAdDetail(videoId, adViewCount);
        System.out.println("Creating ad detail for videoId: " + videoId + ", adViewCount: " + adViewCount);
    }

    //@Retry(name = "adFeignClient", fallbackMethod = "fallbackRetryErrorCase")
    //@CircuitBreaker(name = "adFeignClient", fallbackMethod = "fallbackCase1")
    public ResponseEntity<String> case1() {
        // Supplier 를 CircuitBreaker 로 데코레이팅
        Supplier<ResponseEntity<String>> supplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> adFeignClient.case1());

        // Try.ofSupplier로 Supplier 실행을 시도하고, 실패 시 fallback 메소드 호출
        ResponseEntity<String> response = Try.ofSupplier(supplier)
                .recover(throwable -> fallbackCase1(throwable)).get();

        log.info("case1 method 111111 {}", response.getBody());
        return response;
}

    private ResponseEntity<String> fallbackCase1(Throwable t) {
        return ResponseEntity.internalServerError().body("fallbackCase1 "+t.getMessage());
    }

    private String fallbackRetryErrorCase(Throwable t) {
        return "fallbackRetryErrorCase "+t.getMessage();
    }
}
