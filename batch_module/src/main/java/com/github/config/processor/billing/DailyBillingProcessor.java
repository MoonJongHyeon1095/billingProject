package com.github.config.processor.billing;

import com.github.common.exception.GlobalException;
import com.github.domain.Video;
import com.github.domain.statistic.VideoStatistic;
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
        final Video foundVideo = findTotalViewsByVideoId(videoId);
        final int videoProfit = profitCalculator.calculateVideoProfit(
                foundVideo.getTotalViewCount() - dailyVideoViews,
                dailyVideoViews
        );
        final int adProfit = profitCalculator.calculateAdProfit(
                foundVideo.getTotalAdViewCount() - dailyAdViews,
                dailyAdViews)
                ;
      //  final int adjustedAdProfit = AdProfitCalculator.calculateAdjustedProfit(adProfit, zScore);

        return VideoStatistic.builder()
                .videoId(videoId)
                .dailyBill(videoProfit+adProfit)
                .build();
    }

    private Video findTotalViewsByVideoId(final int videoId){
        return videoMapper.getViewsById(videoId)
                .orElseThrow(()-> new GlobalException(HttpStatus.NOT_FOUND,
                        "일간 정산 작업 중 다음의 videoId로 정보를 찾을 수 없습니다. videoId:" + videoId));
    }
}

