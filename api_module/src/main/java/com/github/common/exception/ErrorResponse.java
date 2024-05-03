package com.github.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private final HttpStatusCode status;
    private final String message;
    private final LocalDateTime timestamp;
}