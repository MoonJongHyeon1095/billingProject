package com.github.config;

import com.github.domain.WatchHistory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ReaderConfiguration {
    /**
     * 자동주입 작동 방식
     * 빈 등록: Spring이 애플리케이션을 시작할 때, BatchConfiguration의 batchDataSource() 메서드를 호출, DataSource 빈을 생성하고 컨텍스트에 등록
     * 의존성 주입: ReaderConfiguration이 생성될 때, Spring은 컨텍스트에서 DataSource 타입의 빈을 찾아 생성자 주입. 이때 BatchConfiguration에서 생성된 DataSource가 주입.
     */
    private final DataSource dataSource;

    public ReaderConfiguration(DataSource batchDataSource) {
        this.dataSource = batchDataSource;
    }

    @Bean
    @JobScope
    public JdbcCursorItemReader<WatchHistory> dailyWatchHistoryReader() {
        JdbcCursorItemReader<WatchHistory> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT * FROM WatchHistory WHERE day = CURRENT_DATE");
        reader.setRowMapper(new WatchHistoryRowMapper());
        return reader;
    }
    @Bean
    @JobScope
    public JdbcCursorItemReader<WatchHistory> weeklyWatchHistoryReader() {
        JdbcCursorItemReader<WatchHistory> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT * FROM WatchHistory WHERE year = YEAR(CURRENT_DATE) AND week = WEEK(CURRENT_DATE)");
        reader.setRowMapper(new WatchHistoryRowMapper());
        return reader;
    }
    @Bean
    @JobScope
    public JdbcCursorItemReader<WatchHistory> monthlyWatchHistoryReader() {
        JdbcCursorItemReader<WatchHistory> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT * FROM WatchHistory WHERE year = YEAR(CURRENT_DATE) AND month = MONTH(CURRENT_DATE)");
        reader.setRowMapper(new WatchHistoryRowMapper());
        return reader;
    }


}
