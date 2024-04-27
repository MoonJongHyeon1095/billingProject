package com.github.config.listener.step_listener;

import com.github.config.processor.statistic.DailyStatisticsProcessor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CacheClearStepListener implements StepExecutionListener {
    private final DailyStatisticsProcessor dailyStatisticsProcessor;

    // 팩토리에서 전달받는 생성자
    public CacheClearStepListener(
            DailyStatisticsProcessor dailyStatisticsProcessor
    ) {
        this.dailyStatisticsProcessor = dailyStatisticsProcessor;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        dailyStatisticsProcessor.clearCache();
        return ExitStatus.COMPLETED;
    }
}
