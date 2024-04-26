package com.github.config.reader;

import com.github.config.db.DataSourceConfiguration;
import com.github.config.mapper.VideoStatisticRowMapper;
import com.github.config.mapper.WatchHistoryRowMapper;
import com.github.domain.statistic.VideoStatistic;
import com.github.domain.WatchHistory;
import com.github.util.DateColumnCalculator;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReaderConfiguration {
    /**
     * 자동주입 작동 방식
     * 빈 등록: Spring이 애플리케이션을 시작할 때, BatchConfiguration의 batchDataSource() 메서드를 호출, DataSource 빈을 생성하고 컨텍스트에 등록
     * 의존성 주입: ReaderConfiguration이 생성될 때, Spring은 컨텍스트에서 DataSource 타입의 빈을 찾아 생성자 주입. 이때 BatchConfiguration에서 생성된 DataSource가 주입.
     */
    private final DateColumnCalculator dateColumnCalculator;
    private final DataSourceConfiguration dataSourceConfiguration;
    public ReaderConfiguration(DateColumnCalculator dateColumnCalculator, DataSourceConfiguration dataSourceConfiguration) {
        this.dateColumnCalculator = dateColumnCalculator;
        this.dataSourceConfiguration = dataSourceConfiguration;
    }

    @Bean
    @JobScope
    public JdbcCursorItemReader<VideoStatistic> dailyBillingReader() {
        JdbcCursorItemReader<VideoStatistic> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSourceConfiguration.dataSource());
        reader.setSql(String.format(
                "SELECT videoId, dailyViewCount, dailyAdViewCount FROM VideoStatistic"
                ));
        reader.setRowMapper(new VideoStatisticRowMapper());
        return reader;
    }

    @Bean
    @JobScope
    public JdbcCursorItemReader<WatchHistory> dailyWatchHistoryReader() {
        JdbcCursorItemReader<WatchHistory> reader = new JdbcCursorItemReader<>();
        final DateColumnCalculator.CustomDate today = dateColumnCalculator.createDateObject();
        final int year = today.getYear();
        final int month = today.getMonth();
        //final int day = today.getDay();
        final int day = 24;
        reader.setDataSource(dataSourceConfiguration.dataSource());
        reader.setSql(String.format(
                "SELECT videoId, playedTime, adViewCount FROM WatchHistory WHERE day = %d AND month = %d AND year = %d", day, month, year));
        reader.setRowMapper(new WatchHistoryRowMapper());
        return reader;
    }
    @Bean
    @JobScope
    public JdbcCursorItemReader<WatchHistory> weeklyWatchHistoryReader() {
        final JdbcCursorItemReader<WatchHistory> reader = new JdbcCursorItemReader<>();
        final DateColumnCalculator.CustomDate today = dateColumnCalculator.createDateObject();
        final int year = today.getYear();
        final int month = today.getMonth();
        final int week = today.getWeek();

        reader.setDataSource(dataSourceConfiguration.dataSource());
        reader.setSql(String.format(
                "SELECT videoId, playedTime, adViewCount FROM WatchHistory WHERE week = %d AND month = %d AND year = %d", week, month, year));
        reader.setRowMapper(new WatchHistoryRowMapper());
        return reader;
    }
    @Bean
    @JobScope
    public JdbcCursorItemReader<WatchHistory> monthlyWatchHistoryReader() {
        final JdbcCursorItemReader<WatchHistory> reader = new JdbcCursorItemReader<>();
        final DateColumnCalculator.CustomDate today = dateColumnCalculator.createDateObject();
        final int year = today.getYear();
        final int month = today.getMonth();

        reader.setDataSource(dataSourceConfiguration.dataSource());
        reader.setSql(String.format(
                "SELECT videoId, playedTime, adViewCount FROM WatchHistory WHERE month = %d AND year = %d", month, year));
        reader.setRowMapper(new WatchHistoryRowMapper());
        return reader;
    }


}
