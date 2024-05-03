package com.github.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class GlobalException extends ResponseStatusException {

    public GlobalException(HttpStatus status, String reason) {
        super(status, reason);
    }
}