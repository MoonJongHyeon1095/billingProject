package com.github.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
public class WatchHistory {
    private String watchHistoryId;
    private String email;
    private Integer videoId;
    private Integer playedTime; //seconds로 환원
    private Integer lastWatched; //seconds로로 환원
    private String deviceUUID;
    private Date createdAt;
    private LocalDateTime watchedAt;
    private Integer adViewCount; //그냥 순서대로 나오는 광고 몇개 봤는지
    private Long numericOrderKey;
    private boolean assignedServer;
}

