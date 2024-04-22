package com.github.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Builder
public class WatchHistory {
    private Integer watchHistoryId;
    private Integer userId;
    private Integer videoId;
    private Integer playedTime; //seconds로 환원
    private Integer lastWatched; //seconds로로 환원
    private String UUID;
    private LocalDateTime createdAt;
    private Integer day;
    private Integer week;
    private Integer month;
    private Integer year;
    private Integer adviewCount; //그냥 순서대로 나오는 광고 몇개 봤는지

    public void setUserId(final Optional<Integer> userIdOptional) {
        if (userIdOptional.isEmpty()) {
            this.userId = null;
        } else {
            this.userId = userIdOptional.orElse(null);
        }
    }

}
