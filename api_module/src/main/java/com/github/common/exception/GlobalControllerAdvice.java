package com.github.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(GlobalException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGlobalException(GlobalException e, ServerWebExchange exchange) {
        log.error("Error occurs", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(e.getStatusCode())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return Mono.just(ResponseEntity.status(e.getStatusCode()).body(errorResponse));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleRuntimeException(RuntimeException e, ServerWebExchange exchange) {
        log.error("Error occurs: {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}