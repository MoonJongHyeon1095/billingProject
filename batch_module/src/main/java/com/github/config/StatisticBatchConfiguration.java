package com.github.config;

import com.github.config.db.DataSourceConfiguration;
import com.github.config.executor.ExecutorServiceConfig;
import com.github.config.listener.job_listener.DailyUpdateJobListener;
import com.github.config.listener.job_listener.JobLoggerListener;
import com.github.config.reader.StatisticReader;
import com.github.config.writer.DailyStatisticWriter;
import com.github.domain.*;
import com.github.mapper.VideoMapper;
import com.github.mapper.VideoStatisticMapper;
import com.github.repository.VideoStatisticRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class StatisticBatchConfiguration {
    private final DataSourceConfiguration dataSourceConfiguration;
    private final DailyStatisticWriter dailyStatisticWriter;
    private final ApplicationEventPublisher eventPublisher;
    private final StatisticReader statisticReader;
    private final ExecutorServiceConfig executorServiceConfig;

    @Bean
    public Job dailyStatisticJobV1(JobRepository jobRepository) {
        return new JobBuilder("dailyStatisticJobV1", jobRepository)
                .preventRestart()
                .listener(new DailyUpdateJobListener(eventPublisher))
                .listener(new JobLoggerListener())
                .start(dailyStatisticStepV1(jobRepository))
                .build();
    }

    @Bean
    public Step dailyStatisticStepV1(JobRepository jobRepository) {
        return new StepBuilder("dailyStatisticStepV1", jobRepository)
                .<WatchHistory, WatchHistory>chunk(
                        2000,
                        dataSourceConfiguration.mariaTransactionManager())
                .reader(statisticReader.buildStatisticReader())
                .writer(dailyStatisticWriter)
                .taskExecutor(
                    //가상 스레드
                    new ConcurrentTaskExecutor(
                            executorServiceConfig.virtualThreadExecutor()
                    )
//                        //플랫폼 스레드
//                        executorServiceConfig.taskExecutor()
                )
                .build();
    }

}