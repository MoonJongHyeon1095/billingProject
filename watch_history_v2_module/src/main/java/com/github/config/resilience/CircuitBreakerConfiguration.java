package com.github.config.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class CircuitBreakerConfiguration {
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        // 1. Circuit breaker Configuration
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                // COUNT_BASED 슬라이딩 창 모드에서 이전 설정과 결합된 값으로 좀 뭐 해보겠다
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10) // 슬라이딩 창 크기
                .minimumNumberOfCalls(5) // 개방 상태로 전환하기 전에 최소한의 호출 횟수
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .failureRateThreshold(50) // 실패율 임계값
                .waitDurationInOpenState(Duration.ofSeconds(10))// 개방 상태 지속 시간
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true) // 자동으로 반-개방 상태로 전환 여부
                .build();

        // 2. Circuit breaker Registry 생성
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);

        // 3. 이름을 지정하여 Circuit breaker 생성
        CircuitBreaker circuitBreaker = registry.circuitBreaker("adFeignClient");

        // 4. 이벤트 리스너 등록
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.info("Circuit Breaker 상태 전환: {}", event))
                .onError(event -> log.info("Circuit Breaker 에러 발생: {}", event.getThrowable().toString()));

        return registry;

    }

}
