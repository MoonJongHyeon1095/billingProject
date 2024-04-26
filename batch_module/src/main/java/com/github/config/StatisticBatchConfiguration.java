package com.github.config;

import com.github.config.db.DataSourceConfiguration;
import com.github.config.listener.job_listener.DailyUpdateJobListener;
import com.github.config.listener.job_listener.MonthlyUpdateJobListener;
import com.github.config.listener.job_listener.WeeklyUpdateJobListener;
import com.github.config.listener.step_listener.CacheClearStepListenerFactory;
import com.github.config.listener.step_listener.LoggerListener;
import com.github.config.processor.DailyStatisticsProcessor;
import com.github.config.processor.MonthlyStatisticsProcessor;
import com.github.config.processor.WeeklyStatisticsProcessor;
import com.github.config.reader.ReaderConfiguration;
import com.github.config.writer.DailyStatisticWriter;
import com.github.config.writer.MonthlyStatisticWriter;
import com.github.config.writer.WeeklyStatisticWriter;
import com.github.domain.*;
import com.github.domain.statistic.MonthlyVideoStatistic;
import com.github.domain.statistic.VideoStatistic;
import com.github.domain.statistic.WeeklyVideoStatistic;
import com.github.mapper.VideoStatisticMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
@EnableBatchProcessing
public class StatisticBatchConfiguration {
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
    @Autowired
    private VideoStatisticMapper videoStatisticMapper;


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
                .listener(CacheClearStepListenerFactory.createWithDaily(dailyStatisticsProcessor))
                .listener(new LoggerListener())
                .writer(dailyStatisticWriter)
                .build();
    }

    @Bean
    public Job weeklyStatisticJob(JobRepository jobRepository) {
        return new JobBuilder("weeklyStatisticJob", jobRepository)
                .listener(new WeeklyUpdateJobListener(videoStatisticMapper))
                .start(weeklyStatisticStep(jobRepository))
                .build();
    }
    @Bean
    public Step weeklyStatisticStep(JobRepository jobRepository) {
        return new StepBuilder("weeklyStatisticStep", jobRepository)
                .<WatchHistory, WeeklyVideoStatistic>chunk(20, batchTransactionManager())
                .reader(readerConfiguration.weeklyWatchHistoryReader())
                .processor(weeklyStatisticsProcessor)
                .listener(CacheClearStepListenerFactory.createWithWeekly(weeklyStatisticsProcessor))
                .listener(new LoggerListener())
                .writer(weeklyStatisticWriter)
                .build();
    }

    @Bean
    public Job monthlyStatisticJob(JobRepository jobRepository) {
        return new JobBuilder("monthlyStatisticJob", jobRepository)
                .listener(new MonthlyUpdateJobListener(videoStatisticMapper))
                .start(monthlyStatisticStep(jobRepository))
                .build();
    }
    @Bean
    public Step monthlyStatisticStep(JobRepository jobRepository) {
        return new StepBuilder("monthlyStatisticStep", jobRepository)
                .<WatchHistory, MonthlyVideoStatistic>chunk(20, batchTransactionManager())
                .reader(readerConfiguration.monthlyWatchHistoryReader())
                .processor(monthlyStatisticsProcessor)
                .listener(CacheClearStepListenerFactory.createWithMonthly(monthlyStatisticsProcessor))
                .listener(new LoggerListener())
                .writer(monthlyStatisticWriter)
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