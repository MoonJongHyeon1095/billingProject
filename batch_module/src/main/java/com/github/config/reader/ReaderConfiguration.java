package com.github.config.reader;

import com.github.config.DataSourceConfiguration;
import com.github.config.mapper.WatchHistoryRowMapper;
import com.github.domain.WatchHistory;
import com.github.util.DateColumnCalculator;
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
    private final DateColumnCalculator dateColumnCalculator;
    private final DataSourceConfiguration dataSourceConfiguration;
    public ReaderConfiguration(DataSource batchDataSource, DateColumnCalculator dateColumnCalculator, DataSourceConfiguration dataSourceConfiguration) {
        this.dataSource = batchDataSource;
        this.dateColumnCalculator = dateColumnCalculator;
        this.dataSourceConfiguration = dataSourceConfiguration;
    }

    @Bean
    @JobScope
    public JdbcCursorItemReader<WatchHistory> dailyWatchHistoryReader() {
        JdbcCursorItemReader<WatchHistory> reader = new JdbcCursorItemReader<>();
        DateColumnCalculator.CustomDate today = dateColumnCalculator.createDateObject();
        int year = today.getYear();
        int month = today.getMonth();
        int day = today.getDay();

        reader.setDataSource(dataSourceConfiguration.dataSource());
        reader.setSql(String.format(
                "SELECT * FROM WatchHistory WHERE year = %d AND month = %d AND day = %d", year, month, day));
        reader.setRowMapper(new WatchHistoryRowMapper());
        return reader;
    }
    @Bean
    @JobScope
    public JdbcCursorItemReader<WatchHistory> weeklyWatchHistoryReader() {
        JdbcCursorItemReader<WatchHistory> reader = new JdbcCursorItemReader<>();
        DateColumnCalculator.CustomDate today = dateColumnCalculator.createDateObject();
        int year = today.getYear();
        int month = today.getMonth();
        int week = today.getWeek();

        reader.setDataSource(dataSourceConfiguration.dataSource());
        reader.setSql(String.format(
                "SELECT * FROM WatchHistory WHERE year = %d AND month = %d AND week = %d", year, month, week));
        reader.setRowMapper(new WatchHistoryRowMapper());
        return reader;
    }
    @Bean
    @JobScope
    public JdbcCursorItemReader<WatchHistory> monthlyWatchHistoryReader() {
        JdbcCursorItemReader<WatchHistory> reader = new JdbcCursorItemReader<>();
        DateColumnCalculator.CustomDate today = dateColumnCalculator.createDateObject();
        int year = today.getYear();
        int month = today.getMonth();

        reader.setDataSource(dataSourceConfiguration.dataSource());
        reader.setSql(String.format(
                "SELECT * FROM WatchHistory WHERE year = %d AND month = %d", year, month));
        reader.setRowMapper(new WatchHistoryRowMapper());
        return reader;
    }


}
