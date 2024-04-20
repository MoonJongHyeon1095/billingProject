package com.github.dto;

import lombok.Getter;

@Getter
public class ViewDto {
    private int videoId;

    public ViewDto(int videoId) {
        this.videoId = videoId;
    }
}
