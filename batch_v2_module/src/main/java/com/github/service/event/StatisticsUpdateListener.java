package com.github.service.event;

import com.github.domain.VideoStatistic;
import com.github.repository.VideoStatisticRepository;
import com.github.util.GlobalSingletonCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
public class StatisticsUpdateListener {
    private final VideoStatisticRepository videoStatisticRepository;

    public StatisticsUpdateListener(VideoStatisticRepository videoStatisticRepository) {
        this.videoStatisticRepository = videoStatisticRepository;
    }

    @EventListener
    public void handleUpdateStatisticsEvent(UpdateStatisticsEvent event) {
        List<VideoStatistic> statList = GlobalSingletonCache.getInstance().getCacheData();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Flux.fromIterable(statList)
                .flatMap(stat -> videoStatisticRepository.findOneByVideoIdAndCreatedAt(today, stat.getVideoId())
                        .flatMap(foundStat -> {
                            if (foundStat != null) {
                                return videoStatisticRepository.updateDailyStatistic(
                                        VideoStatistic.builder()
                                                .videoId(stat.getVideoId())
                                                .dailyViewCount(foundStat.getDailyViewCount() + stat.getDailyViewCount())
                                                .dailyWatchedTime(foundStat.getDailyWatchedTime() + stat.getDailyWatchedTime())
                                                .dailyAdViewCount(foundStat.getDailyAdViewCount() + stat.getDailyAdViewCount())
                                                .createdAt(today)
                                                .build()
                                );
                            } else {
                                return videoStatisticRepository.insertDailyStatistic(stat);
                            }
                        })
                )
                .doFinally(signalType -> {
                    GlobalSingletonCache.getInstance().clearCache();
                    log.info("Cache cleared after processing UpdateStatisticsEvent.");
                })
                .subscribe(
                        null,
                        error -> log.error("Error updating statistics: " + error),
                        () -> log.info("Statistics update completed.")
                );
    }
}
