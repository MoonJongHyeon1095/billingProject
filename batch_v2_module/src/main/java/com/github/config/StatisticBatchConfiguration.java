package com.github.config;

import com.github.config.db.DataSourceConfiguration;
import com.github.config.executor.ExecutorServiceConfig;
import com.github.config.listener.job_listener.DailyUpdateJobListener;
import com.github.config.listener.job_listener.JobLoggerListener;
import com.github.config.partition.RangePartitioner;
import com.github.config.processor.DailyStatisticsProcessor;
import com.github.config.reader.StatisticReader;
import com.github.config.writer.DailyStatisticWriter;
import com.github.domain.WatchHistory;
import com.github.mapper.VideoMapper;
import com.github.mapper.VideoStatisticMapper;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
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
    private final VideoStatisticMapper videoStatisticMapper;
    private final StatisticReader statisticReader;
    //가상스레드 생성
    private final ExecutorServiceConfig executorServiceConfig;

    @Bean
    public Job dailyStatisticJobV2(JobRepository jobRepository) {
        return new JobBuilder("dailyStatisticJobV2", jobRepository)
                .preventRestart()
                .listener(new DailyUpdateJobListener(videoStatisticMapper))
                .listener(new JobLoggerListener())
                .start(dailyStatisticStepV2(jobRepository))
                .build();
    }
    /**
     * processor 제거, 전역 캐시 객체에 바로 누적
     */
    @Bean
    public Step dailyStatisticStepV2(JobRepository jobRepository) {
        return new StepBuilder("dailyStatisticStepV2", jobRepository)
                .<WatchHistory, WatchHistory>chunk(
                        2000,
                        dataSourceConfiguration.batchTransactionManager())
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