package com.github.config.processor;

import com.github.common.exception.GlobalException;
import com.github.domain.VideoStatistic;
import com.github.dto.VideoViewsDto;
import com.github.mapper.VideoMapper;
import com.github.util.ProfitCalculator;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DailyBillingProcessor implements ItemProcessor<VideoStatistic, VideoStatistic> {
    private final ProfitCalculator profitCalculator;
    private final VideoMapper videoMapper;

    public DailyBillingProcessor(final ProfitCalculator profitCalculator, final VideoMapper videoMapper) {
        this.profitCalculator = profitCalculator;
        this.videoMapper = videoMapper;
    }

    @Override
    public VideoStatistic process(final VideoStatistic item) throws Exception {
        final int videoId = item.getVideoId();
        final int dailyVideoViews = item.getDailyViewCount();
        final int dailyAdViews = item.getDailyAdViewCount();
       // final double zScore = item.getZScore();
        final VideoViewsDto foundVideo = findTotalViewsByVideoId(videoId);
        int prevViews = foundVideo.getTotalViewCount() - dailyVideoViews;
        int prevAdViews = foundVideo.getTotalAdViewCount() - dailyAdViews;
        prevViews = prevViews < 0 ? 0 : prevViews;
        prevAdViews = prevAdViews <0 ? 0 : prevAdViews;
        final int videoProfit = profitCalculator.calculateVideoProfit(
                prevViews,
                dailyVideoViews
        );
        final int adProfit = profitCalculator.calculateAdProfit(
                prevAdViews,
                dailyAdViews
        );
      //  final int adjustedAdProfit = AdProfitCalculator.calculateAdjustedProfit(adProfit, zScore);

        return VideoStatistic.builder()
                .videoId(videoId)
                .dailyVideoProfit(videoProfit)
                .dailyAdProfit(adProfit)
                .build();
    }

    private VideoViewsDto findTotalViewsByVideoId(final int videoId){
        return videoMapper.getTotalViewsAndTotalAdViewsById(videoId)
                .orElseThrow(()-> new GlobalException(HttpStatus.NOT_FOUND,
                        "일간 정산 작업 중 다음의 videoId로 정보를 찾을 수 없습니다. videoId:" + videoId));
    }
}

