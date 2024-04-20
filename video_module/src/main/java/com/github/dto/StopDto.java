package com.github.dto;

import lombok.Getter;

@Getter
public class StopDto {
    private int stoppedTime;

    public StopDto(int stoppedTime) {
        this.stoppedTime = stoppedTime;
    }
}
