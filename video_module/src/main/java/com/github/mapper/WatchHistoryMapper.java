package com.github.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.github.domain.WatchHistory;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface WatchHistoryMapper {

    // userId로 가장 최근의 WatchHistory를 찾는 메서드
    @Select(
            "SELECT lastWatched FROM WatchHistory " +
                    "WHERE email = #{email} " +
                    "ORDER BY watchedAt DESC " +
                    "LIMIT 1")
    Optional<Integer> findLastWatchedByEmail(@Param("email") String email);

    // UUID로 가장 최근의 WatchHistory를 찾는 메서드
    @Select(
            "SELECT lastWatched FROM WatchHistory " +
                    "WHERE deviceUUID = #{deviceUUID} " +
                    "ORDER BY watchedAt DESC LIMIT 1")
    Optional<Integer> findLastWatchedByDeviceUUID(@Param("deviceUUID") String deviceUUID);

}
