package com.github.util;

import org.springframework.stereotype.Component;

/**
 * 단가 계산기 (정산 금액 계산이 아님! 조회 1회당 단가를 계산)
 */
@Component
public class ProfitCalculator {
    /**
     * 첫 번째 구간: 0~99,999 (단가 1원)
     * 두 번째 구간: 100,000~499,999 (단가 1.1원)
     * 세 번째 구간: 500,000~999,999 (단가 1.3원)
     * 네 번째 구간: 1,000,000 이상 (단가 1.5원)
     */
    private static final double[] VIDEO_RATES = {1.0, 1.1, 1.3, 1.5};
    private static final int[] VIDEO_THRESHOLDS = {100_000, 500_000, 1_000_000};

    private static final double[] AD_RATES = {10.0, 12.0, 15.0, 20.0};
    private static final int[] AD_THRESHOLDS = {100_000, 500_000, 1_000_000};

    /**
     * 가이드에서 요구한 영상 조회 1회 당 통합 단가를 반환하는 메서드.
     * 하지만 영상별 광고 조회수 카운트 기능을 따로 구현했기에, 단가를 통합하지 않으려 한다
     *
     * 가이드를 따르지 않고, 이 메서드를 대체할 계산방법
     * 해당 영상 일간, 주간, 월간 조회수 * 영상단가
     * 해당 영상 일간, 주간, 월간 광고조회수 * 광고단가
     */
    public double calculatePrice(int totalViews) {
        double videoRevenue = calculateVideoProfit(totalViews);
        double adRevenue = calculateAdProfit(totalViews);

        return Math.floor(videoRevenue + adRevenue); // 1원 단위 이하 절사
    }

    public double calculateVideoProfit(int totalViews){
        double price = 0;
        int remainingViews = totalViews;
        int lastThreshold = 0;

        for (int i = 0; i < VIDEO_THRESHOLDS.length; i++) {
            int threshold = VIDEO_THRESHOLDS[i];
            int diff = threshold - lastThreshold;

            if (remainingViews >= diff) { // 구간을 넘어서는 조회수가 있는지 확인
                price += diff * VIDEO_RATES[i];
                remainingViews -= diff; // 계산 후 남은 조회수를 감소
                lastThreshold = threshold; // 다음 구간을 위해 업데이트
            } else {
                price += remainingViews * VIDEO_RATES[i];
                remainingViews = 0; // 모든 조회수를 처리했으므로 0으로 초기화
                break;
            }
        }

        // 구간을 벗어난(100만 이상) 최대구간 조회수에 대해 최대 단가를 적용
        if (remainingViews > 0) {
            price += remainingViews * VIDEO_RATES[VIDEO_RATES.length - 1];
        }

        return Math.floor(price); // 1원 단위 이하 절사
    }

    public double calculateAdProfit(int totalViews) {
        double price = 0;
        //int adCount = (int) Math.floor(videoLengthInMinutes / 5.0); // 광고 수, 5분당 1개

        int remainingViews = totalViews;
        int lastThreshold = 0;
        for (int i = 0; i < AD_THRESHOLDS.length; i++) {
            int threshold = AD_THRESHOLDS[i];
            int diff = threshold - lastThreshold;

            if (remainingViews >= diff) { // 구간을 넘어서는 조회수가 있는지 확인
                price += diff * AD_RATES[i];
                remainingViews -= diff; // 계산 후 남은 조회수를 감소
                lastThreshold = threshold; // 다음 구간을 위해 업데이트
            } else {
                price += remainingViews * AD_RATES[i];
                remainingViews = 0; // 모든 조회수를 처리했으므로 0으로 초기화
                break;
            }
        }

        // 마지막 구간에 대한 계산
        if (remainingViews > 0) {
            price += remainingViews * AD_RATES[AD_RATES.length - 1];
        }

        return Math.floor(price); // 1원 단위 이하 절사
    }


}
