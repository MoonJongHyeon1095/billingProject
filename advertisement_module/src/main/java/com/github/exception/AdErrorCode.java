package com.github.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AdErrorCode {
    AD_NOT_FOUND(HttpStatus.NOT_FOUND, "[ERROR] 재생된 광고가 없습니다."),
    INVALID_DATA_INTEGRITY(HttpStatus.CONFLICT, "광고 조회수 증가가 제대로 반영되지 않았습니다.");

    private final HttpStatus status;
    private final String message;

    AdErrorCode(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }
}