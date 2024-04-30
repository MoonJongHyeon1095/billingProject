package com.github.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateCalculator {
    public static LocalDate[] getMonSun() {
        // 한국 시간대 기준으로 현재 날짜 구하기
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 한국의 Locale을 사용하여 주의 첫 날을 월요일로 설정
        WeekFields weekFields = WeekFields.of(Locale.KOREA);

        // 현재 주의 월요일 구하기
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));
        // 현재 주의 일요일 구하기.
        LocalDate sunday = monday.plusDays(6);

        return new LocalDate[]{monday, sunday};
    }


    public static LocalDate[] getFirstAndLastDayOfMonth() {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 현재 달의 첫 날
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        // 현재 달의 마지막 날
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        return new LocalDate[]{firstDayOfMonth, lastDayOfMonth};
    }

}
