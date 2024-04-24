package com.github.config;

import com.github.domain.Statistic;
import com.github.domain.WatchHistory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    private ReaderConfiguration readerConfiguration;
    @Autowired
    private StatisticsProcessor statisticsProcessor;
    @Autowired
    private StatisticsWriter statisticsWriter;


    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username}")
    private String dataSourceUsername;

    @Value("${spring.datasource.password}")
    private String dataSourcePassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dataSourceDriverClassName;

    @Bean
    public DataSource batchDataSource() {
        return DataSourceBuilder.create()
                .url(dataSourceUrl)
                .username(dataSourceUsername)
                .password(dataSourcePassword)
                .driverClassName(dataSourceDriverClassName)
                .build();
    }

    @Bean
    public JdbcTransactionManager batchTransactionManager() {
        return new JdbcTransactionManager(batchDataSource());
    }

    @Bean
    public Job dailyStatisticsJob(JobRepository jobRepository) {
        return new JobBuilder("dailyStaticJob", jobRepository)
                .start(dailyStatisticStep(jobRepository))
                .build();
    }
    @Bean
    public Step dailyStatisticStep(JobRepository jobRepository) {
        return new StepBuilder("dailyStaticStep", jobRepository)
                .<WatchHistory, Map<Integer, Statistic>>chunk(10, batchTransactionManager())
                .reader(readerConfiguration.dailyWatchHistoryReader())
                .processor(statisticsProcessor)
                .writer(statisticsWriter)
                .build();
    }


}