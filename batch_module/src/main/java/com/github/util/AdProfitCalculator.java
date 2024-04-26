package com.github.util;

public class AdProfitCalculator {
    public static int calculateAdjustedProfit(int baseProfit, double zScore) {
        double adjustmentFactor = 0.0; // 기본 값

        if (zScore < -1.0) {
            adjustmentFactor = 0.95; // 95% 감액
        } else if (zScore < -0.5) {
            adjustmentFactor = 0.90; // 90% 감액
        } else if (zScore < -0.4) {
            adjustmentFactor = 0.80; // 80% 감액
        } else if (zScore < -0.3) {
            adjustmentFactor = 0.70; // 70% 감액
        } else if (zScore < -0.2) {
            adjustmentFactor = 0.60; // 60% 감액
        } else if (zScore < -0.1) {
            adjustmentFactor = 0.50; // 50% 감액
        } else if (zScore < 0.0) {
            adjustmentFactor = 0.40; // 40% 감액
        } else if (zScore < 0.1) {
            adjustmentFactor = 0.30; // 30% 감액
        } else if (zScore < 0.5) {
            adjustmentFactor = 0.20; // 20% 감액
        } else if (zScore < 1.0) {
            adjustmentFactor = 0.10; // 10% 감액
        } else {
            adjustmentFactor = 0; // 원래 금액
        }

        // 적용된 정산 금액 계산
        double adjustedProfit;
        adjustedProfit = baseProfit * (1 - adjustmentFactor);

        // 소수점 이하 버리고 정수로 반환
        return (int) Math.floor(adjustedProfit);
    }

}
