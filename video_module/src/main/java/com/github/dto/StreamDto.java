package com.github.dto;

import lombok.Getter;

@Getter
public class StreamDto {
    private int videoId;

    public StreamDto(int videoId) {
        this.videoId = videoId;
    }
}
