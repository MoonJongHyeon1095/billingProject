package com.github.config;

import com.github.config.db.DataSourceConfiguration;
import com.github.config.listener.step_listener.LoggerListener;
import com.github.config.processor.billing.DailyBillingProcessor;
import com.github.config.reader.ReaderConfiguration;
import com.github.config.writer.billing.DailyBillingWriter;
import com.github.domain.statistic.VideoStatistic;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
public class BillBatchConfiguration {
    private final DataSourceConfiguration dataSourceConfiguration;
    private final ReaderConfiguration readerConfiguration;
    private final DailyBillingProcessor dailyBillingProcessor;
    private final DailyBillingWriter dailyBillingWriter;

    public BillBatchConfiguration(
            DataSourceConfiguration dataSourceConfiguration,
            ReaderConfiguration readerConfiguration,
            DailyBillingProcessor dailyBillingProcessor,
            DailyBillingWriter dailyBillingWriter
    ) {
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.readerConfiguration = readerConfiguration;
        this.dailyBillingProcessor = dailyBillingProcessor;
        this.dailyBillingWriter = dailyBillingWriter;
    }

    @Bean(name = "billTransactionManager") //transactionManager라고 명시하지 않으면 찾지 못한다.
    public JdbcTransactionManager batchTransactionManager() {
        return new JdbcTransactionManager(dataSourceConfiguration.dataSource());
    }
    @Bean
    public Job dailyBillingJob(JobRepository jobRepository) {
        return new JobBuilder("dailyBillingJob", jobRepository)
                .preventRestart()
                .start(dailyBillingStep(jobRepository))
                .build();
    }
    @Bean
    public Step dailyBillingStep(JobRepository jobRepository) {
        return new StepBuilder("dailyBillingStep", jobRepository)
                .<VideoStatistic, VideoStatistic>chunk(20, batchTransactionManager())
                .reader(readerConfiguration.dailyBillingReader())
                .processor(dailyBillingProcessor)
                .listener(new LoggerListener())
                .writer(dailyBillingWriter)
                .build();
    }


}