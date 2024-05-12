package com.github.config.mapper;

import com.github.domain.WatchHistory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WatchHistoryRowMapper implements RowMapper<WatchHistory> {
    @Override
    public WatchHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
        return WatchHistory.builder()
                .videoId(rs.getInt("videoId"))
                .playedTime(rs.getInt("playedTime"))
                .adViewCount(rs.getInt("adViewCount"))
                //.createdAt(rs.getDate("createdAt"))
                .numericOrderKey(rs.getLong("numericOrderKey"))
                .assignedServer(rs.getBoolean("assignedServer"))
                .build();
    }
}

