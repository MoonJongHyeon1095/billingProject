package com.github.config.processor;

import com.github.common.exception.GlobalException;
import com.github.domain.statistic.VideoStatistic;
import com.github.mapper.VideoMapper;
import com.github.util.AdProfitCalculator;
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
//        final int videoViews = item.getDailyViewCount();
//        final int adViews = item.getDailyAdViewCount();
        final double zScore = item.getZScore();
        final int totalViews = findTotalViewsByVideoId(videoId);
        final int videoProfit = (int) profitCalculator.calculateVideoProfit(totalViews);
        final int adProfit = (int) profitCalculator.calculateAdProfit(totalViews);
        final int adjustedAdProfit = AdProfitCalculator.calculateAdjustedProfit(adProfit, zScore);

        return VideoStatistic.builder()
                .videoId(videoId)
                .dailyBill(videoProfit+adjustedAdProfit)
                .build();
    }

    private Integer findTotalViewsByVideoId(final int videoId){
        return videoMapper.getTotalViewById(videoId)
                .orElseThrow(()-> new GlobalException(HttpStatus.NOT_FOUND,
                        "일간 정산 작업 중 다음의 videoId로 정보를 찾을 수 없습니다. videoId:" + videoId));
    }
}

