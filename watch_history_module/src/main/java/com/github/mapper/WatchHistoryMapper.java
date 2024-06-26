package com.github.mapper;

import com.github.domain.WatchHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WatchHistoryMapper {

    @Insert("INSERT INTO WatchHistory " +
            "(watchHistoryId, videoId, email, playedTime, lastWatched, adviewCount, deviceUUID, " +
            "createdAt, watchedAt, numericOrderKey, assignedServer)" +
            " VALUES (#{watchHistoryId}, #{videoId}, #{email}, #{playedTime}, #{lastWatched}, #{adviewCount}, #{deviceUUID}, " +
            "#{createdAt}, #{watchedAt}, #{numericOrderKey}, #{assignedServer})")
    Integer insertWatchHistory(WatchHistory watchHistory);


}
