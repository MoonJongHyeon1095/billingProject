package com.github.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccViewDto {
    private Integer videoId;
    private Integer totalViewCount;
}
