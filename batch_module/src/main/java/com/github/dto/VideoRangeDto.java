package com.github.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VideoRangeDto {
    private Integer firstVideoId;
    private Integer lastVideoId;
}
