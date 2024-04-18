package com.github.common.response;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS("success", "0000"),
    FAILURE("failure", "9000");

    private final String message;
    private final String code;

    ResponseCode(final String message, final String code) {
        this.message = message;
        this.code = code;
    }
}