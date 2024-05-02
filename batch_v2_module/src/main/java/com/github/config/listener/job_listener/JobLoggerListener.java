package com.github.config.listener.job_listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobLoggerListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Job 완료 후 로깅
        long readCount = jobExecution.getStepExecutions().stream()
                .mapToLong(stepExecution -> stepExecution.getReadCount())
                .sum();
        long writeCount = jobExecution.getStepExecutions().stream()
                .mapToLong(stepExecution -> stepExecution.getWriteCount())
                .sum();

        log.info("Total records read: {}", readCount);
        log.info("Total records written: {}", writeCount);

        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("Job failed with status: {}", jobExecution.getStatus());
        } else {
            log.info("Job completed successfully with status: {}", jobExecution.getStatus());
        }
    }
}
