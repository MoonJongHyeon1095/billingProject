package com.github.config.mapper;

import com.github.domain.AdvertisementDetail;
import com.github.domain.WatchHistory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdvertisementDetailRowMapper  implements RowMapper<AdvertisementDetail> {
    @Override
    public AdvertisementDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AdvertisementDetail.builder()
                .adPriority(rs.getInt("adPriority"))
                .viewCount(rs.getInt("viewCount"))
                .build();
    }
}
