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
    private final DailyStatisticsProcessor dailyStatisticsProcessor;
    private final DailyStatisticWriter dailyStatisticWriter;
    private final VideoStatisticMapper videoStatisticMapper;
    private final VideoMapper videoMapper;
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

    @Bean
    public RangePartitioner partitioner(){
        RangePartitioner partitioner = new RangePartitioner(videoMapper);
        return partitioner;
    }

    /**
     * girdSize 만큼 가상스레드를 생성, dailyStatisticStep 실행
     *
     * TaskExecutorPartitionHandler의 taskExecutor: 파티션 병렬 실행
     * 전체 파티션 작업의 병렬 실행을 조정합니다. 이 핸들러는 주어진 TaskExecutor를 사용하여 각 파티션을 병렬로 실행합니다.
     * 각 파티션이 동시에 실행될 때, 이 TaskExecutor는 병렬 실행을 담당하며,
     * 지정된 gridSize에 따라 여러 파티션을 동시에 실행합니다.
     */
    @Bean
    public Step partitionStep(
            JobRepository jobRepository
    ) {

        return new StepBuilder("partitionedStep", jobRepository)
                .partitioner("dailyStatisticStepV2", partitioner())
                .step(dailyStatisticStepV2(jobRepository))
                .taskExecutor(executorServiceConfig.taskExecutor())
                .gridSize(2)
                .build();
    }

    /**
     dailyStatisticStep의 taskExecutor:
     - 파티션 내부의 병렬 작업을 제어, dailyStatisticStep은 파티션을 통해 실행되는 개별 스텝
     - 이 스텝에도 taskExecutor가 주어졌는데, 이는 스텝 내부에서 병렬 처리를 수행할 때 사용
     - 각 파티션은 독립적으로 실행되며, 이 내부에서 병렬로 처리할 때 taskExecutor를 사용
     - 이는 스텝 내부의 작업(예: 데이터 읽기, 처리, 쓰기)을 병렬로 수행하는 데 활용
     */
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
                        //플랫폼 스레드
//                        executorServiceConfig.taskExecutor()
                )
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
                .tasklet(clearDataTasklet(), dataSourceConfiguration.batchTransactionManager())
                .build();
    }
    @Bean
    public Tasklet clearDataTasklet() {
        return (StepContribution contribution, ChunkContext context) -> {
            // 데이터 삭제 SQL 실행
            context.getStepContext().getStepExecution().getJobExecution()
                    .getExecutionContext().put("executionContext", "Clear data in target tables.");

            String sql = "DELETE FROM VideoStatistic"; // 대상 테이블의 데이터 삭제
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourceConfiguration.routingDataSource());
            jdbcTemplate.execute(sql);

            return RepeatStatus.FINISHED; // 작업 종료
        };
    }

}