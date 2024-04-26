package com.github.config.mapper;

import com.github.domain.Video;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoRowMapper implements RowMapper<Video> {

    @Override
    public Video mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Video.builder()
                .videoId(rs.getInt("videoId"))
                .totalViewCount(rs.getInt("totalViewCount"))
                .build();
    }
}
