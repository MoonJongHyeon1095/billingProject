package com.github.config;

import com.github.config.db.DataSourceConfiguration;
import com.github.config.listener.job_listener.DailyUpdateJobListener;
import com.github.config.listener.step_listener.CacheClearStepListener;
import com.github.config.listener.step_listener.LoggerListener;
import com.github.config.processor.statistic.DailyStatisticsProcessor;
import com.github.config.reader.ReaderConfiguration;
import com.github.config.writer.statistic.DailyStatisticWriter;
import com.github.domain.*;
import com.github.domain.statistic.VideoStatistic;
import com.github.mapper.VideoStatisticMapper;
import org.springframework.batch.core.*;
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
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class StatisticBatchConfiguration {
    private final DataSourceConfiguration dataSourceConfiguration;
    private final ReaderConfiguration readerConfiguration;
    private final DailyStatisticsProcessor dailyStatisticsProcessor;
    private final DailyStatisticWriter dailyStatisticWriter;
    private final VideoStatisticMapper videoStatisticMapper;
    private final DataSource dataSource;

    public StatisticBatchConfiguration(
            DataSourceConfiguration dataSourceConfiguration,
            ReaderConfiguration readerConfiguration,
            DailyStatisticsProcessor dailyStatisticsProcessor,
            DailyStatisticWriter dailyStatisticWriter,
            VideoStatisticMapper videoStatisticMapper,
            DataSource dataSource) {
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.readerConfiguration = readerConfiguration;
        this.dailyStatisticsProcessor = dailyStatisticsProcessor;
        this.dailyStatisticWriter = dailyStatisticWriter;
        this.videoStatisticMapper = videoStatisticMapper;
        this.dataSource = dataSource;
    }

    @Bean(name = "transactionManager") //transactionManager라고 명시하지 않으면 찾지 못한다.
    public JdbcTransactionManager batchTransactionManager() {
        return new JdbcTransactionManager(dataSourceConfiguration.dataSource());
    }

    @Bean
    public Job dailyStatisticJob(JobRepository jobRepository) {
        return new JobBuilder("dailyStaticJob", jobRepository)
                .preventRestart()
                .listener(new DailyUpdateJobListener(videoStatisticMapper))
                .start(dailyStatisticStep(jobRepository))
                .build();
    }
    @Bean
    public Step dailyStatisticStep(JobRepository jobRepository) {
        return new StepBuilder("dailyStaticStep", jobRepository)
                .<WatchHistory, VideoStatistic>chunk(20, batchTransactionManager())
                .reader(readerConfiguration.dailyWatchHistoryReader())
                .processor(dailyStatisticsProcessor)
                .listener(new CacheClearStepListener(dailyStatisticsProcessor))
                .listener(new LoggerListener())
                .writer(dailyStatisticWriter)
                .build();
    }
    @Bean
    public Job clearDataJob(JobRepository jobRepository) {
        return new JobBuilder("clearDataJob", jobRepository)
                .start(clearDataStep(jobRepository))
                .build();
    }

    @Bean
    public Step clearDataStep(JobRepository jobRepository) {
        return new StepBuilder("clearDataStep", jobRepository)
                .tasklet(clearDataTasklet(), batchTransactionManager())
                .build();
    }
    @Bean
    public Tasklet clearDataTasklet() {
        return (StepContribution contribution, ChunkContext context) -> {
            // 데이터 삭제 SQL 실행
            context.getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().put("executionContext", "Clear data in target tables.");

            String sql = "DELETE FROM VideoStatistic"; // 대상 테이블의 데이터 삭제
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourceConfiguration.dataSource());
            jdbcTemplate.execute(sql);

            return RepeatStatus.FINISHED; // 작업 종료
        };
    }
}