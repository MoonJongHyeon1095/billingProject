package com.github.service;

import com.github.feignclient.AdFeignClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Slf4j
@Service
public class ResilienceService {
    private final AdFeignClient adFeignClient;
    private final CircuitBreaker circuitBreaker;

    public ResilienceService(AdFeignClient adFeignClient, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.adFeignClient = adFeignClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("adFeignClient");
    }

    /**
     * 광고 도메인 서버로 FeignClient POST
     *
      Supplier :
     - get() 메소드를 통해서만 값을 제공. et() 메소드 호출될 때까지 어떠한 연산도 수행하지 않습니다.
     - Supplier를 정의할 때는 단지 어떻게 값을 가져올 것인가를 설명
     - 그리고 Try.ofSupplier(supplier)와 get() 호출을 통해 실제로 Supplier 내부의 로직이 실행되고, 결과값을 가져오게 됩니다.
     *
     * @param videoId
     * @param adViewCount
     */
    protected void createAdDetail(int videoId, int adViewCount) {
        // 이 메소드는 Feign 클라이언트가 실제로 호출되는 부분입니다.
        // 실제 구현에서는 여기에 HTTP 요청을 보내는 로직이 포함됩니다.
        Supplier<ResponseEntity<String>> supplier = CircuitBreaker
                .decorateSupplier(
                        circuitBreaker, () -> adFeignClient.createAdDetail(videoId, adViewCount));

        // Try.ofSupplier로 Supplier 실행을 시도하고, 실패 시 fallback 메소드 호출
        ResponseEntity<String> response = Try.ofSupplier(supplier)
                .recover(throwable -> fallbackCreatedAd(throwable)).get();

    }

    private ResponseEntity<String> fallbackCreatedAd(Throwable t) {
        return ResponseEntity.internalServerError().body("fallbackCreatedAd "+t.getMessage());
    }

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

    private  ResponseEntity<String>  fallbackRetryErrorCase(Throwable t) {
        return ResponseEntity.internalServerError().body("fallbackRetry "+t.getMessage());
    }
}
