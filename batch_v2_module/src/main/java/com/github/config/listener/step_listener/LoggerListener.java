package com.github.config.listener.step_listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class LoggerListener implements StepExecutionListener {


    @Override
    public void beforeStep(StepExecution stepExecution){


    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long selectedCount = stepExecution.getReadCount();
        log.info("Selected {} records", selectedCount);

        // 삽입한 레코드 수 기록
        long insertedCount = stepExecution.getWriteCount();
        log.info("Inserted {} records", insertedCount);
        return stepExecution.getExitStatus();
    }
}