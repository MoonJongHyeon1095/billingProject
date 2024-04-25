package com.github.config;

import com.github.config.listener.CacheClearStepListener;
import com.github.config.listener.StepExecutionListenerImpl;
import com.github.config.processor.DailyStatisticsProcessor;
import com.github.config.processor.MonthlyStatisticsProcessor;
import com.github.config.processor.WeeklyStatisticsProcessor;
import com.github.config.reader.ReaderConfiguration;
import com.github.config.writer.DailyStatisticWriter;
import com.github.config.writer.MonthlyStatisticWriter;
import com.github.config.writer.WeeklyStatisticWriter;
import com.github.domain.VideoStatistic;
import com.github.domain.WatchHistory;
import com.github.util.DateColumnCalculator;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    private DataSourceConfiguration dataSourceConfiguration;
    @Autowired
    private ReaderConfiguration readerConfiguration;
    @Autowired
    private DailyStatisticsProcessor dailyStatisticsProcessor;
    @Autowired
    private DailyStatisticWriter dailyStatisticWriter;
    @Autowired
    private WeeklyStatisticsProcessor weeklyStatisticsProcessor;
    @Autowired
    private WeeklyStatisticWriter weeklyStatisticWriter;
    @Autowired
    private MonthlyStatisticsProcessor monthlyStatisticsProcessor;
    @Autowired
    private MonthlyStatisticWriter monthlyStatisticWriter;

    @Bean(name = "transactionManager") //transactionManager라고 명시하지 않으면 찾지 못한다.
    public JdbcTransactionManager batchTransactionManager() {
        return new JdbcTransactionManager(dataSourceConfiguration.dataSource());
    }

    @Bean
    public Job dailyStatisticJob(JobRepository jobRepository) {
        return new JobBuilder("dailyStaticJob", jobRepository)
                .start(dailyStatisticStep(jobRepository))
                .build();
    }
    @Bean
    public Step dailyStatisticStep(JobRepository jobRepository) {
        return new StepBuilder("dailyStaticStep", jobRepository)
                .<WatchHistory, VideoStatistic>chunk(20, batchTransactionManager())
                .reader(readerConfiguration.dailyWatchHistoryReader())
                .processor(dailyStatisticsProcessor)
                .listener((ChunkListener)new CacheClearStepListener())
                .listener(new StepExecutionListenerImpl())
                .writer(dailyStatisticWriter)
                .build();
    }

    @Bean
    public Job weeklyStatisticJob(JobRepository jobRepository) {
        return new JobBuilder("weeklyStatisticJob", jobRepository)
                .start(weeklyStatisticStep(jobRepository))
                .build();
    }
    @Bean
    public Step weeklyStatisticStep(JobRepository jobRepository) {
        return new StepBuilder("weeklyStatisticStep", jobRepository)
                .<WatchHistory, VideoStatistic>chunk(20, batchTransactionManager())
                .reader(readerConfiguration.weeklyWatchHistoryReader())
                .processor(weeklyStatisticsProcessor)
                .listener((ChunkListener)new CacheClearStepListener())
                .listener(new StepExecutionListenerImpl())
                .writer(weeklyStatisticWriter)
                .build();
    }

    @Bean
    public Job monthlyStatisticJob(JobRepository jobRepository) {
        return new JobBuilder("monthlyStatisticJob", jobRepository)
                .start(monthlyStatisticStep(jobRepository))
                .build();
    }
    @Bean
    public Step monthlyStatisticStep(JobRepository jobRepository) {
        return new StepBuilder("monthlyStatisticStep", jobRepository)
                .<WatchHistory, VideoStatistic>chunk(20, batchTransactionManager())
                .reader(readerConfiguration.monthlyWatchHistoryReader())
                .processor(monthlyStatisticsProcessor)
                .listener((ChunkListener)new CacheClearStepListener())
                .listener(new StepExecutionListenerImpl())
                .writer(monthlyStatisticWriter)
                .build();
    }


}