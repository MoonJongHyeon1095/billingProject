package com.github.dto;

import lombok.Getter;

import java.util.Optional;

@Getter
public class StopDto {
    private int videoId;
    //로그인 한 경우만 존재
    private Optional<Integer> userId;
    private int playedTime;
    private int lastWatched;
    private int adViewCount;

    public StopDto(final int videoId,final Integer userId,final int playedTime,final int lastWatched, final int adViewCount) {
        this.videoId = videoId;
        this.userId = Optional.ofNullable(userId);
        this.playedTime = playedTime;
        this.lastWatched = lastWatched;
        this.adViewCount = adViewCount;
    }
}
