package com.github.util;

import com.github.domain.AdRangeEnum;
import com.github.domain.VideoRangeEnum;
import org.springframework.stereotype.Component;

/**
 * 총 정산액 계산기
 */
@Component
public class ProfitCalculator {
    /**
     * 첫 번째 구간: 0~99,999 (단가 1원)
     * 두 번째 구간: 100,000~499,999 (단가 1.1원)
     * 세 번째 구간: 500,000~999,999 (단가 1.3원)
     * 네 번째 구간: 1,000,000 이상 (단가 1.5원)
     */
    private static final double[] VIDEO_PRICE = {1.0, 1.1, 1.3, 1.5};
    private static final int[] VIDEO_VIEW_RANGE = {0, 100_000, 500_000, 1_000_000};

    private static final double[] AD_PRICE = {10.0, 12.0, 15.0, 20.0};
    private static final int[] AD_VIEW_RANGE = {0, 100_000, 500_000, 1_000_000};

    public int calculateVideoProfit(final int prevViews, final int increment){
        final int rangeIncrement = countRangeIncrement(prevViews, increment);

        if(rangeIncrement==0){
            return (int) (VIDEO_PRICE[VideoRangeEnum.getRange(prevViews)] * increment);
        }else if(rangeIncrement==1){
            final int currentViews = prevViews + increment;
            final int firstGap = VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1] - prevViews;
            final int secondGap = currentViews - VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1];
            return (int) Math.floor(VIDEO_PRICE[VideoRangeEnum.getRange(prevViews)] * firstGap
                    + VIDEO_PRICE[VideoRangeEnum.getRange(prevViews)+1] * secondGap);
        }else if(rangeIncrement==2){
            final int currentViews = prevViews + increment;
            final int firstGap = VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1] - prevViews;
            final int secondGap = VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+2] - VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1];
            final int thirdGap = currentViews - VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+2];
            return (int) Math.floor(VIDEO_PRICE[VideoRangeEnum.getRange(prevViews)] * firstGap
                    + VIDEO_PRICE[VideoRangeEnum.getRange(prevViews)+1] * secondGap
                    + VIDEO_PRICE[VideoRangeEnum.getRange(prevViews)+2] * thirdGap
            );
        }else if(rangeIncrement==3){
            final int currentViews = prevViews + increment;
            final int firstGap = VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1] - prevViews;
            final int secondGap = VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+2] - VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1];
            final int thirdGap = VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+3] - VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+2];
            final int forthGap = currentViews - VIDEO_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+3];
            return (int) Math.floor(VIDEO_PRICE[VideoRangeEnum.getRange(prevViews)] * firstGap
                    + VIDEO_PRICE[VideoRangeEnum.getRange(prevViews)+1] * secondGap
                    + VIDEO_PRICE[VideoRangeEnum.getRange(prevViews)+2] * thirdGap
                    +VIDEO_PRICE[VideoRangeEnum.getRange(prevViews)+3] * forthGap
            );
        }
        return 0;
    }

    public int calculateAdProfit(int prevViews, int increment){
        final int rangeIncrement = countAdRangeIncrement(prevViews, increment);

        if(rangeIncrement==0){
            return (int) (AD_PRICE[VideoRangeEnum.getRange(prevViews)] * increment);
        }else if(rangeIncrement==1){
            final int currentViews = prevViews + increment;
            final int firstGap = AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1] - prevViews;
            final int secondGap = currentViews - AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1];
            return (int) Math.floor(AD_PRICE[VideoRangeEnum.getRange(prevViews)] * firstGap
                    + AD_PRICE[VideoRangeEnum.getRange(prevViews)+1] * secondGap);
        }else if(rangeIncrement==2){
            final int currentViews = prevViews + increment;
            final int firstGap = AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1] - prevViews;
            final int secondGap = AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+2] - AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1];
            final int thirdGap = currentViews - AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+2];
            return (int) Math.floor(AD_PRICE[VideoRangeEnum.getRange(prevViews)] * firstGap
                    + AD_PRICE[VideoRangeEnum.getRange(prevViews)+1] * secondGap
                    + AD_PRICE[VideoRangeEnum.getRange(prevViews)+2] * thirdGap
            );
        }else if(rangeIncrement==3){
            final int currentViews = prevViews + increment;
            final int firstGap = AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1] - prevViews;
            final int secondGap = AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+2] - AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+1];
            final int thirdGap = AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+3] - AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+2];
            final int forthGap = currentViews - AD_VIEW_RANGE[VideoRangeEnum.getRange(prevViews)+3];
            return (int) Math.floor(AD_PRICE[VideoRangeEnum.getRange(prevViews)] * firstGap
                    + AD_PRICE[VideoRangeEnum.getRange(prevViews)+1] * secondGap
                    + AD_PRICE[VideoRangeEnum.getRange(prevViews)+2] * thirdGap
                    +AD_PRICE[VideoRangeEnum.getRange(prevViews)+3] * forthGap
            );
        }
        return 0;
    }

    private int countRangeIncrement(final int prevViews, final int increment){
        return VideoRangeEnum.getRange(prevViews + increment) - VideoRangeEnum.getRange(prevViews);
    }

    private int countAdRangeIncrement(final int prevViews, final int increment){
        return AdRangeEnum.getRange(prevViews + increment) - AdRangeEnum.getRange(prevViews);
    }
}
