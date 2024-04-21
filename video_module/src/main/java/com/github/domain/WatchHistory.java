package com.github.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WatchHistory {
    private Integer watchHistoryId;
    private Integer userId;
    private Integer videoId;
    private Integer playedTime;
    private Integer lastWatched;
    private String UUID;
    private LocalDateTime createdAt;
    private Integer day;
    private Integer week;
    private Integer month;
    private Integer year;
    private Integer adviewCount;
}
