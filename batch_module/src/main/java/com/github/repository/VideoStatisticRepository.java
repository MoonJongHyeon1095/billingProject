package com.github.repository;

import com.github.domain.VideoStatistic;
import com.github.dto.VideoStatDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

public interface VideoStatisticRepository extends ReactiveCrudRepository<VideoStatistic, Integer> {

    @Query("UPDATE VideoStatistic SET dailyWatchedTime = :#{#vs.dailyWatchedTime}, " +
            "dailyViewCount = :#{#vs.dailyViewCount}, " +
            "dailyAdViewCount = :#{#vs.dailyAdViewCount} " +
            "WHERE createdAt = :#{#vs.createdAt} AND videoId = :#{#vs.videoId}")
    Mono<Integer> updateDailyStatistic(final VideoStatistic vs);

    @Query("INSERT INTO VideoStatistic " +
            "(videoId, dailyWatchedTime, dailyViewCount, dailyAdViewCount, createdAt) " +
            "VALUES (:#{#vs.videoId}, :#{#vs.dailyWatchedTime}, :#{#vs.dailyViewCount}, " +
            ":#{#vs.dailyAdViewCount}, :#{#vs.createdAt})")
    Mono<Void> insertDailyStatistic(final VideoStatistic vs);

    @Query("UPDATE VideoStatistic SET dailyVideoProfit = :dailyVideoProfit, dailyAdProfit = :dailyAdProfit " +
            "WHERE createdAt = :createdAt AND videoId = :videoId")
    Mono<Void> updateDailyBill(@Param("videoId") final Integer videoId,
                               @Param("dailyVideoProfit") final Integer dailyVideoProfit,
                               @Param("dailyAdProfit") final Integer dailyAdProfit,
                               @Param("createdAt") final LocalDate createdAt);

    @Query("SELECT EXISTS (SELECT 1 FROM VideoStatistic WHERE videoId = :videoId)")
    Mono<Boolean> existsVideoStatisticByVideoId(@Param("videoId") final int videoId);

    @Query("SELECT videoId, dailyViewCount, dailyWatchedTime, dailyAdViewCount, createdAt " +
            "FROM VideoStatistic " +
            "WHERE createdAt = :createdAt AND videoId = :videoId")
    Mono<VideoStatDto> findOneByVideoIdAndCreatedAt(@Param("createdAt") final LocalDate createdAt,
                                                    @Param("videoId") final int videoId);
}
