package com.github.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode {
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 좀 똑바로 입력해보십쇼."),
    INVALID_AUTHENTICATION_NUM(HttpStatus.BAD_REQUEST, "인증번호를 틀리셨네요."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다만.");

    private final HttpStatus status;
    private final String message;

    AuthErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
