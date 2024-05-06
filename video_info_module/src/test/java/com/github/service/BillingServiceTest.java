package com.github.service;

import com.github.controller.response.UserBillResponse;
import com.github.dto.DailyBillDto;
import com.github.mapper.VideoMapper;
import com.github.util.DateCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillingServiceTest {
    @Mock
    private VideoMapper videoMapper;

    @InjectMocks
    private BillingService billingService;

    @Test
    public void testFindDailyBillingByEmail() {
        String email = "test@example.com";
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<DailyBillDto> mockDailyBill = Arrays.asList(
                DailyBillDto.builder()
                        .videoId(1)
                        .dailyVideoProfit(10)
                        .dailyAdProfit(10)
                        .createdAt(today)
                        .build());

        when(videoMapper.findDailyProfitByEmail(email, today)).thenReturn(mockDailyBill);

        UserBillResponse response = billingService.findDailyBillingByEmail(email);

        assertNotNull(response);
    }

    @Test
    public void testFindWeeklyBillingByEmail() {
        String email = "test@example.com";
        List<Integer> videoIdList = Arrays.asList(1, 2, 3);
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate[] monDaySunDay = DateCalculator.getMonSun();
        List<DailyBillDto> mockWeeklyBill = Arrays.asList(
                DailyBillDto.builder()
                        .videoId(1)
                        .dailyVideoProfit(10)
                        .dailyAdProfit(10)
                        .createdAt(today)
                        .build());

        when(videoMapper.findVideoIdListByEmail(email)).thenReturn(videoIdList);
        for(Integer videoId : videoIdList) {
            when(videoMapper.findPeriodProfitByVideoId(videoId, monDaySunDay[0], monDaySunDay[1]))
                    .thenReturn(mockWeeklyBill);
        }

        UserBillResponse response = billingService.findWeeklyBillingByEmail(email);

        assertNotNull(response);
    }

    @Test
    public void testFindMonthlyBillingByEmail() {
        String email = "test@example.com";
        List<Integer> videoIdList = Arrays.asList(1, 2, 3);
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate[] firstAndLastDayOfMonth = DateCalculator.getFirstAndLastDayOfMonth();
        List<DailyBillDto> mockMonthlyBill = Arrays.asList(
                DailyBillDto.builder()
                        .videoId(1)
                        .dailyVideoProfit(10)
                        .dailyAdProfit(10)
                        .createdAt(today)
                        .build());

        when(videoMapper.findVideoIdListByEmail(email)).thenReturn(videoIdList);
        for(Integer videoId : videoIdList) {
            when(videoMapper.findPeriodProfitByVideoId(videoId, firstAndLastDayOfMonth[0], firstAndLastDayOfMonth[1]))
                    .thenReturn(mockMonthlyBill);
        }

        UserBillResponse response = billingService.findMonthlyBillingByEmail(email);

        assertNotNull(response);
    }
}
