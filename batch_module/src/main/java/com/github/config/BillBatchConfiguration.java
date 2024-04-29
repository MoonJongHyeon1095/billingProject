package com.github.config;

import com.github.config.db.DataSourceConfiguration;
import com.github.config.executor.ExecutorServiceConfig;
import com.github.config.listener.job_listener.JobLoggerListener;
import com.github.config.listener.step_listener.LoggerListener;
import com.github.config.processor.DailyBillingProcessor;
import com.github.config.reader.ReaderConfiguration;
import com.github.config.writer.DailyBillingWriter;
import com.github.domain.statistic.VideoStatistic;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

@AllArgsConstructor
@Configuration
@EnableBatchProcessing
public class BillBatchConfiguration {
    private final DataSourceConfiguration dataSourceConfiguration;
    private final ReaderConfiguration readerConfiguration;
    private final DailyBillingProcessor dailyBillingProcessor;
    private final DailyBillingWriter dailyBillingWriter;
    private final ExecutorServiceConfig executorServiceConfig;

    @Bean
    public Job dailyBillingJob(JobRepository jobRepository) {
        return new JobBuilder("dailyBillingJob", jobRepository)
                .preventRestart()
                .listener(new JobLoggerListener())
                .start(dailyBillingStep(jobRepository))
                .build();
    }
    @Bean
    public Step dailyBillingStep(JobRepository jobRepository) {
        return new StepBuilder("dailyBillingStep", jobRepository)
                .<VideoStatistic, VideoStatistic>chunk(20, dataSourceConfiguration.batchTransactionManager())
                .reader(readerConfiguration.dailyBillingReader())
                .processor(dailyBillingProcessor)
                .listener(new LoggerListener())
                .writer(dailyBillingWriter)
                .taskExecutor(
                        new ConcurrentTaskExecutor(
                                executorServiceConfig.virtualThreadExecutor()
                        )
                )
                .build();
    }
}
