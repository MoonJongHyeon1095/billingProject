package com.github.controller.response;

import lombok.Getter;

@Getter
public class VideoResponse {
    private final String message;
    public VideoResponse(final String message) {
        this.message = message;
    }
}
