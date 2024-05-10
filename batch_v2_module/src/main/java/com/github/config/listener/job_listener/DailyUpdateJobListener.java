package com.github.config.listener.job_listener;

import com.github.domain.VideoStatistic;
import com.github.dto.VideoStatDto;
import com.github.mapper.VideoStatisticMapper;
import com.github.util.GlobalSingletonCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

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
        List<VideoStatistic> statList = globalCache.getCacheData();
        for(VideoStatistic newStat: statList){
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
            //LocalDate today = LocalDate.parse("2024-05-09");
            //행이 존재하면
            Optional<VideoStatDto> foundStat = videoStatisticMapper.findOneByVideoIdAndCreatedAt(today, newStat.getVideoId());
            if(foundStat.isPresent()){
                videoStatisticMapper.updateDailyStatistic(
                        VideoStatistic.builder()
                                .videoId(newStat.getVideoId())
                                .dailyViewCount(foundStat.get().getDailyViewCount() + newStat.getDailyViewCount())
                                .dailyWatchedTime(foundStat.get().getDailyWatchedTime() + newStat.getDailyWatchedTime())
                                .dailyAdViewCount(foundStat.get().getDailyAdViewCount() + newStat.getDailyAdViewCount())
                                .createdAt(today)
                                .build()
                );
            }else{
                videoStatisticMapper.insertDailyStatistic(
                        VideoStatistic.builder()
                                .videoId(newStat.getVideoId())
                                .dailyViewCount(newStat.getDailyViewCount())
                                .dailyWatchedTime(newStat.getDailyWatchedTime())
                                .dailyAdViewCount(newStat.getDailyAdViewCount())
                                .createdAt(today)
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
