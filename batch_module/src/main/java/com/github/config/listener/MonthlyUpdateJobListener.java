package com.github.config.listener;

import com.github.domain.VideoStatistic;
import com.github.mapper.VideoStatisticMapper;
import com.github.util.GlobalSingletonCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

@Slf4j
public class MonthlyUpdateJobListener implements JobExecutionListener {
    private final VideoStatisticMapper videoStatisticMapper;  // 필드 주입
    private final GlobalSingletonCache globalCache = GlobalSingletonCache.getInstance();
    @Autowired
    public MonthlyUpdateJobListener(VideoStatisticMapper videoStatisticMapper) {
        this.videoStatisticMapper = videoStatisticMapper;
    }
    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Optional: Add any pre-job logic here
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        List<VideoStatistic> statList = globalCache.getCacheData();
        for(VideoStatistic newStat: statList){

            try {
                // 먼저 업데이트 시도
                videoStatisticMapper.updateMonthlyStatistic(
                        VideoStatistic.builder()
                                .videoId(newStat.getVideoId())
                                .monthlyViewCount(newStat.getMonthlyViewCount())
                                .monthlyWatchedTime(newStat.getMonthlyWatchedTime())
                                .build()
                );
            } catch (EmptyResultDataAccessException e) {
                // 업데이트에 실패하면(행이 없어서) 삽입 시도
                videoStatisticMapper.insertMonthlyStatistic(
                        VideoStatistic.builder()
                                .videoId(newStat.getVideoId())
                                .monthlyViewCount(newStat.getMonthlyViewCount())
                                .monthlyWatchedTime(newStat.getMonthlyWatchedTime())
                                .build()
                );
            }
        }
        globalCache.clearCache();

        if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            log.info("Cache cleared after job completion.");
        } else {
            log.info("Job did not complete successfully, but cache still cleared.");
        }
    }

}
