package com.github.config.listener.step_listener;

import com.github.config.processor.statistic.DailyStatisticsProcessor;
import com.github.config.processor.statistic.MonthlyStatisticsProcessor;
import com.github.config.processor.statistic.WeeklyStatisticsProcessor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CacheClearStepListener implements StepExecutionListener {
    private final DailyStatisticsProcessor dailyStatisticsProcessor;
    private final WeeklyStatisticsProcessor weeklyStatisticsProcessor;
    private final MonthlyStatisticsProcessor monthlyStatisticsProcessor;

    // 팩토리에서 전달받는 생성자
    public CacheClearStepListener(
            DailyStatisticsProcessor dailyStatisticsProcessor,
            WeeklyStatisticsProcessor weeklyStatisticsProcessor,
            MonthlyStatisticsProcessor monthlyStatisticsProcessor) {
        this.dailyStatisticsProcessor = dailyStatisticsProcessor;
        this.weeklyStatisticsProcessor = weeklyStatisticsProcessor;
        this.monthlyStatisticsProcessor = monthlyStatisticsProcessor;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // 프로세스가 끝난 후 캐시 비우기
        if (dailyStatisticsProcessor != null) {
            dailyStatisticsProcessor.clearCache();
        }
        if (weeklyStatisticsProcessor != null) {
            weeklyStatisticsProcessor.clearCache();
        }
        if (monthlyStatisticsProcessor != null) {
            monthlyStatisticsProcessor.clearCache();
        }

        return ExitStatus.COMPLETED;
    }
}
