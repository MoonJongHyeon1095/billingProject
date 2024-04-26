package com.github.config.mapper;

import com.github.domain.statistic.VideoStatistic;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoStatisticRowMapper implements RowMapper<VideoStatistic> {
    @Override
    public VideoStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
        return VideoStatistic.builder()
                .videoId(rs.getInt("videoId"))
                .dailyViewCount(rs.getInt("dailyViewCount"))
                .dailyAdViewCount(rs.getInt("dailyAdViewCount"))
                .build();
    }
}
