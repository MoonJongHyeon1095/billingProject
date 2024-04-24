package com.github.config.listener;

import com.github.config.processor.DailyStatisticsProcessor;
import com.github.config.processor.MonthlyStatisticsProcessor;
import com.github.config.processor.WeeklyStatisticsProcessor;
import com.github.domain.VideoStatistic;
import com.github.domain.WatchHistory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;

public class CacheClearStepListener extends StepListenerSupport<WatchHistory, VideoStatistic> {

    @Autowired
    private DailyStatisticsProcessor dailyStatisticsProcessor;
    @Autowired
    private WeeklyStatisticsProcessor weeklyStatisticsProcessor;
    @Autowired
    private MonthlyStatisticsProcessor monthlyStatisticsProcessor;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // 프로세스가 끝난 후 캐시 비우기
        dailyStatisticsProcessor.clearCache();
        weeklyStatisticsProcessor.clearCache();
        monthlyStatisticsProcessor.clearCache();

        return ExitStatus.COMPLETED;
    }
}
