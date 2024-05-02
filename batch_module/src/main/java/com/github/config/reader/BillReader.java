package com.github.config.reader;

import com.github.config.mapper.VideoStatisticRowMapper;
import com.github.config.mapper.WatchHistoryRowMapper;
import com.github.domain.VideoStatistic;
import com.github.domain.WatchHistory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Configuration
@AllArgsConstructor
public class BillReader {
    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<VideoStatistic> reader() {

        //String today = LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String today = "2024-05-01";

        return new JdbcPagingItemReaderBuilder<VideoStatistic>()
                .name("reader")
                .pageSize(20)
                .fetchSize(20)
                .dataSource(dataSource)
                .rowMapper(new VideoStatisticRowMapper())
                .queryProvider(pagingQueryProvider())
                .parameterValues(Map.of(
                        "today", today
//                    "startVideoId", startVideoId,
//                    "endVideoId", endVideoId
                ))
                .build();
    }

    @Bean
    private PagingQueryProvider pagingQueryProvider() {

        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("SELECT videoId, dailyViewCount, dailyAdViewCount, createdAt");
        queryProvider.setFromClause("FROM VideoStatistic");
        //queryProvider.setWhereClause("WHERE createdAt = :today AND videoId BETWEEN :startVideoId AND :endVideoId");
        queryProvider.setWhereClause("WHERE createdAt = :today");
        queryProvider.setSortKey("videoId");

        try {
            return queryProvider.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
