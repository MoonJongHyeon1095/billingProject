package com.github.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Builder
public class WatchHistory {
    private Integer watchHistoryId;
    private Integer userId;
    private Integer videoId;
    private Integer playedTime; //초로 환원
    private Integer lastWatched; //초로 환원
    private String UUID;
    private LocalDateTime createdAt;
    private Integer day;
    private Integer week;
    private Integer month;
    private Integer year;
    private Integer adviewCount; //그냥 순서대로 나오는 광고 몇개 봤는지

    public void setUserId(final Optional<Integer> userIdOptional) {
        if (userIdOptional.isEmpty()) {
            // Handle null scenario, either set to a default value or handle with an exception
            this.userId = null;
        } else {
            // Set the userId to the value or default to null if Optional is empty
            this.userId = userIdOptional.orElse(null);
        }
    }

}
