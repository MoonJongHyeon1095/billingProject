package com.github.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.github.domain.WatchHistory;

import java.util.Optional;

@Mapper
public interface WatchHistoryMapper {

    // watchHistoryId로 WatchHistory를 찾는 메서드
    WatchHistory findOneById(@Param("watchHistoryId") String watchHistoryId);

    // userId로 가장 최근의 WatchHistory를 찾는 메서드
    Optional<Integer> findLastWatchedByEmail(@Param("email") String email);

    // UUID로 가장 최근의 WatchHistory를 찾는 메서드
    Optional<Integer> findLastWatchedByDeviceUUID(@Param("deviceUUID") String deviceUUID);

    // WatchHistory 객체를 데이터베이스에 삽입하는 메서드
    Integer insertWatchHistory(WatchHistory watchHistory);

}
