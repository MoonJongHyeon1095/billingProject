package com.github.feignclient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "adFeignClient", url = "http://localhost:8083")
public interface AdFeignClient {

    @PostMapping("/v1/advertisement/{videoId}/{adViewCount}")
    ResponseEntity<String> createAdDetail(
            @PathVariable("videoId") int videoId,
            @PathVariable("adViewCount") int adViewCount
    );

    default void fallbackCreateAdDetail(int videoId, int adViewCount, Throwable t) {
        // 여기에 fallback 로직 구현

    }


    @GetMapping("/v1/advertisement/error/case1")
    ResponseEntity<String> case1();


}
