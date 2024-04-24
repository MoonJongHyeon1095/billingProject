package com.github.config.mapper;

import com.github.domain.WatchHistory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

// Define the row mapping for your entity (WatchHistory)
public class WatchHistoryRowMapper implements RowMapper<WatchHistory> {
    @Override
    public WatchHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
        return WatchHistory.builder()
                .watchHistoryId(rs.getInt("watchHistoryId"))
                .videoId(rs.getInt("videoId"))
                .playedTime(rs.getInt("playedTime"))
                .year(rs.getInt("year"))
                .month(rs.getInt("month"))
                .day(rs.getInt("day"))
                .build();
    }

}

