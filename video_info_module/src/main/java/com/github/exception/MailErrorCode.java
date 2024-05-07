package com.github.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MailErrorCode {
    CREATE_PDF_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "[ERROR] PDF 파일 생성 중 오류 발생"),
    SEND_MAIN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "[ERROR] 메일발송실패");

    private final HttpStatus status;
    private final String message;

    MailErrorCode(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }
}
