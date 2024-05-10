package com.github.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Builder
public class WatchHistory {
    private String watchHistoryId;
    private String email;
    private Integer videoId;
    private Integer playedTime; //seconds로 환원
    private Integer lastWatched; //seconds로로 환원
    private String deviceUUID;
    private LocalDate createdAt;
    private LocalDateTime watchedAt;
    private Integer adviewCount; //그냥 순서대로 나오는 광고 몇개 봤는지
    private boolean assignedBoolean;

    public void setEmail(final Optional<String> emailOptional) {
        if (emailOptional.isEmpty()) {
            this.email = null;
        } else {
            this.email = emailOptional.orElse(null);
        }
    }

}
