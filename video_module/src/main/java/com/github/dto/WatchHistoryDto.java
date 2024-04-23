package com.github.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class WatchHistoryDto {
    private int videoId;
    @Setter
    //로그인 한 경우만 존재
    private Optional<Integer> userId = Optional.empty();
    private int playedTime;
    private int lastWatched;
    private int adViewCount;

    public WatchHistoryDto(final int videoId,final Integer userId,final int playedTime,final int lastWatched, final int adViewCount) {
        this.videoId = videoId;
        this.userId = Optional.ofNullable(userId);
        this.playedTime = playedTime;
        this.lastWatched = lastWatched;
        this.adViewCount = adViewCount;
    }
}
