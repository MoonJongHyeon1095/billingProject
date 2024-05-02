package com.github.mapper;

import com.github.domain.WatchHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface WatchHistoryMapper {

    @Insert("INSERT INTO WatchHistory " +
            "(watchHistoryId, videoId, email, playedTime, lastWatched, adviewCount, deviceUUID, " +
            "createdAt, watchedAt, numericOrderKey)" +
            " VALUES (#{watchHistoryId}, #{videoId}, #{email}, #{playedTime}, #{lastWatched}, #{adviewCount}, #{deviceUUID}, " +
            "#{createdAt}, #{watchedAt}), #{numericOrderKey}")
    Integer insertWatchHistory(WatchHistory watchHistory);

}
