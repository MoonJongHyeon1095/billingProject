package com.github.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class WatchHistoryDto {
    private int videoId;
    @Setter
    //로그인 한 경우만 존재
    private Optional<String> email = Optional.empty();
    private int playedTime;
    private int lastWatched;
    private int adViewCount;
    private LocalDate createdAt;
    private LocalDateTime watchedAt;

    public WatchHistoryDto(final int videoId,final String email,final int playedTime,final int lastWatched, final int adViewCount) {
        this.videoId = videoId;
        this.email = Optional.ofNullable(email);
        this.playedTime = playedTime;
        this.lastWatched = lastWatched;
        this.adViewCount = adViewCount;
    }
}
