package com.github.service.event;

import com.github.domain.VideoStatistic;
import com.github.dto.VideoStatDto;
import com.github.repository.VideoStatisticRepository;
import com.github.util.GlobalSingletonCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
public class StatisticsUpdateListener {
    private final VideoStatisticRepository videoStatisticRepository;
    private final TransactionalOperator transactionalOperator;

    public StatisticsUpdateListener(VideoStatisticRepository videoStatisticRepository, TransactionalOperator transactionalOperator) {
        this.videoStatisticRepository = videoStatisticRepository;
        this.transactionalOperator = transactionalOperator;
    }

    @EventListener
    public void handleUpdateStatisticsEvent(UpdateStatisticsEvent event) {
        List<VideoStatistic> statList = GlobalSingletonCache.getInstance().getCacheData();
        //log.info("캐시사이즈: " + statList.size());
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        Flux.fromIterable(statList)
                .flatMap(stat -> processStatistic(stat, today))
                .doFinally(signalType -> {
                    GlobalSingletonCache.getInstance().clearCache();
                    log.info("Cache cleared after processing UpdateStatisticsEvent.");
                })
                .as(transactionalOperator::transactional)  // 하나의 큰 트랜잭션으로 처리
                .subscribe(
                        null,
                        error -> log.error("Error updating statistics: " + error.getMessage(), error),
                        () -> log.info("Statistics update completed.")
                );
    }

    private Mono<Void> processStatistic(VideoStatistic stat, LocalDate today) {
        //log.info("processStatistic: " + stat.getVideoId());
        return videoStatisticRepository.findOneByVideoIdAndCreatedAt(today, stat.getVideoId())
                .flatMap(foundStat -> updateOrInsertStatistic(stat, foundStat, today))
                .then();  // 이전 단계의 결과를 Void로 변환하여 반환
    }

    private Mono<Void> updateOrInsertStatistic(VideoStatistic stat, VideoStatDto foundStat, LocalDate today) {
        if (foundStat != null) {
            log.info("Updating statistics for video ID: " + stat.getVideoId());
            return videoStatisticRepository.updateDailyStatistic(
                    VideoStatistic.builder()
                            .videoId(stat.getVideoId())
                            .dailyViewCount(foundStat.getDailyViewCount() + stat.getDailyViewCount())
                            .dailyWatchedTime(foundStat.getDailyWatchedTime() + stat.getDailyWatchedTime())
                            .dailyAdViewCount(foundStat.getDailyAdViewCount() + stat.getDailyAdViewCount())
                            .createdAt(today)
                            .build()
            ).then();
        } else {
            log.info("Inserting new statistics for video ID: " + stat.getVideoId());
            return videoStatisticRepository.insertDailyStatistic(stat);
        }
    }
}
