package com.github.service;

import com.github.controller.response.UserBillResponse;
import com.github.domain.VideoStatistic;
import com.github.dto.DailyBillDto;
import com.github.mapper.VideoMapper;
import com.github.util.DateCalculator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
@Service
@AllArgsConstructor
public class BillingService {
    private final VideoMapper videoMapper;

    /**
     * 일간 정산
     * @param email
     * @return
     */
    public UserBillResponse findDailyBillingByEmail(final String email){
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<DailyBillDto> userBill = findDailyProfitByEmail(email, today);
        return UserBillResponse.from(userBill);
    }

    /**
     * 주간 정산
     * @param email
     */
    public UserBillResponse findWeeklyBillingByEmail(final String email) {
        List<Integer> videoIdList = findVideoIdListByEmail(email);
        LocalDate[] monDaySunDay = DateCalculator.getMonSun();
        List<DailyBillDto> weeklyBill = new ArrayList<>();
        for(int videoId : videoIdList) {
            List<DailyBillDto> weeklyStat = findPeriodProfitByVideoId(videoId, monDaySunDay[0], monDaySunDay[1]);
            weeklyBill.addAll(weeklyStat);
        }

        return UserBillResponse.from(weeklyBill);
    }

    /**
     * 월간 정산
     * @param email
     */
    public UserBillResponse findMonthlyBillingByEmail(final String email) {
        List<Integer> videoIdList = findVideoIdListByEmail(email);
        LocalDate[] firstAndLastDayOfMonth = DateCalculator.getFirstAndLastDayOfMonth();
        List<DailyBillDto> monthlyBill = new ArrayList<>();
        for(int videoId : videoIdList) {
            List<DailyBillDto> monthlyStat = findPeriodProfitByVideoId(
                    videoId, firstAndLastDayOfMonth[0], firstAndLastDayOfMonth[1]);
            monthlyBill.addAll(monthlyStat);
        }
        return UserBillResponse.from(monthlyBill);
    }


    private List<DailyBillDto> findDailyProfitByEmail(final String email, final LocalDate createdAt){
        return videoMapper.findDailyProfitByEmail(email, createdAt);
    }

    private List<DailyBillDto> findPeriodProfitByVideoId(final Integer videoId, final LocalDate start, final LocalDate end) {
        return videoMapper.findPeriodProfitByVideoId(videoId, start, end);
    }


    private List<Integer> findVideoIdListByEmail(final String email){
        return videoMapper.findVideoIdListByEmail(email);
    }


}
