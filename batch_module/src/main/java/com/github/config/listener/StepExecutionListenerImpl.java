package com.github.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class StepExecutionListenerImpl implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution){
        int selectedCount = (int) stepExecution.getExecutionContext().get("selectedCount");
        log.info("Selected {} records", selectedCount);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // 삽입한 레코드 수 기록
        int insertedCount = (int) stepExecution.getExecutionContext().get("insertedCount");
        log.info("Inserted {} records", insertedCount);
        return stepExecution.getExitStatus();
    }
}