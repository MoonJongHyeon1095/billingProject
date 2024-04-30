package com.github.config.listener.job_listener;

import com.github.domain.VideoStatistic;
import com.github.mapper.VideoStatisticMapper;
import com.github.util.GlobalSingletonCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
public class DailyUpdateJobListener implements JobExecutionListener {
    private final VideoStatisticMapper videoStatisticMapper;  // 필드 주입
    private final GlobalSingletonCache globalCache = GlobalSingletonCache.getInstance();
    @Autowired
    public DailyUpdateJobListener(VideoStatisticMapper videoStatisticMapper) {
        this.videoStatisticMapper = videoStatisticMapper;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        //globalCache.setDailyZScores();
        List<VideoStatistic> statList = globalCache.getCacheData();
        for(VideoStatistic newStat: statList){
            //LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
            LocalDate today = LocalDate.parse("2024-04-29");
            try {
                // 먼저 업데이트 시도
                Integer updated = videoStatisticMapper.updateDailyStatistic(
                        VideoStatistic.builder()
                                .videoId(newStat.getVideoId())
                                .dailyViewCount(newStat.getDailyViewCount())
                                .dailyWatchedTime(newStat.getDailyWatchedTime())
                                .dailyAdViewCount(newStat.getDailyAdViewCount())
                                .createdAt(today)
                                //.zScore(newStat.getZScore())
                                .build()
                );

                if(updated==0){
                    // 업데이트에 실패하면(행이 없어서) 삽입 시도
                    videoStatisticMapper.insertDailyStatistic(
                            VideoStatistic.builder()
                                    .videoId(newStat.getVideoId())
                                    .dailyViewCount(newStat.getDailyViewCount())
                                    .dailyWatchedTime(newStat.getDailyWatchedTime())
                                    .dailyAdViewCount(newStat.getDailyAdViewCount())
                                    .createdAt(today)
                                    //.zScore(newStat.getZScore())
                                    .build()
                    );
                }

            } catch (EmptyResultDataAccessException e) {
                // 업데이트에 실패하면(행이 없어서) 삽입 시도
                videoStatisticMapper.insertDailyStatistic(
                        VideoStatistic.builder()
                                .videoId(newStat.getVideoId())
                                .dailyViewCount(newStat.getDailyViewCount())
                                .dailyWatchedTime(newStat.getDailyWatchedTime())
                                .dailyAdViewCount(newStat.getDailyAdViewCount())
                                //.zScore(newStat.getZScore())
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
