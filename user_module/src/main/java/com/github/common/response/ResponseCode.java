package com.github.common.response;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS("success", "0000"),
    FAILURE("failure", "9000");

    private final String description;
    private final String code;

    ResponseCode(final String description, final String code) {
        this.description = description;
        this.code = code;
    }
}