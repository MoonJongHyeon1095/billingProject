package com.github.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VideoViewsDto {
    private Integer totalViewCount;
    private Integer totalAdViewCount;
}
