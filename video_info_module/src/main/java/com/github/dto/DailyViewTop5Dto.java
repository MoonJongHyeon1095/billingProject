package com.github.dto;

import com.github.domain.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyViewTop5Dto {
    private Integer videoId;
    private String title;
    private Integer dailyViewCount;
}
