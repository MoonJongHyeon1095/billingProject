package com.github.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.github.domain.WatchHistory;

import java.util.Optional;

@Mapper
public interface WatchHistoryMapper {

    // watchHistoryId로 WatchHistory를 찾는 메서드
    WatchHistory findOneById(@Param("watchHistoryId") Integer watchHistoryId);

    // userId로 가장 최근의 WatchHistory를 찾는 메서드
    Optional<Integer> findLastWatchedByUserId(@Param("userId") Integer userId);

    // UUID로 가장 최근의 WatchHistory를 찾는 메서드
    Optional<Integer> findLastWatchedByUUID(@Param("UUID") String UUID);

    // WatchHistory 객체를 데이터베이스에 삽입하는 메서드
    Integer insertWatchHistory(WatchHistory watchHistory);

}
