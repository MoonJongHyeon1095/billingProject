package com.github.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum VideoErrorCode {
    INSERT_WATCH_HISTORY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "[ERROR] 시청기록 생성 실패."),
    VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND, "[ERROR] 해당 영상을 찾을 수 없습니다."),
    REDIS_LOCK_NOT_AVAILABLE(HttpStatus.CONFLICT, "[ERROR] 레디스 락을 획득할 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    VideoErrorCode(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }
}