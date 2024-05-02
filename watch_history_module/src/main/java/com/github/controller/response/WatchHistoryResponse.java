package com.github.controller.response;

import lombok.Getter;

@Getter
public class WatchHistoryResponse {
    private final String message;
    public WatchHistoryResponse(final String message) {
        this.message = message;
    }
}
