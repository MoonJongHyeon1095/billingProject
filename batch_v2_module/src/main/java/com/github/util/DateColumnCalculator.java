package com.github.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;

@Component
public class DateColumnCalculator {
    // 한국 시간대(KST) 설정
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    // 현재 한국 시간대의 날짜 가져오기 (요청 시마다 갱신)
    public LocalDateTime getCurrentTime() {
        return ZonedDateTime.now(SEOUL_ZONE).toLocalDateTime();
    }

    public CustomDate createDateObject(){
        LocalDateTime now = getCurrentTime(); // 요청 시마다 현재 시간 계산
        final int year = now.getYear();
        final int month = now.getMonthValue();
        final int week = getWeek(now);
        final int day = now.getDayOfMonth();

        return new CustomDate(year, month, week, day);
    }

    //1년을 주로 환원
    private int getWeek(LocalDateTime now){
        // 주 시작 요일을 월요일로 설정하고, 최소 일수를 1로 설정
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);

        // 주 번호 계산
        return now.get(weekFields.weekOfYear());
    }

    @Getter
    public static class CustomDate {
        private final int year;
        private final int month;
        private final int week;
        private final int day;

        public CustomDate(final int year, final int month, final int week, final int day) {
            this.year = year;
            this.month = month;
            this.week = week;
            this.day = day;
        }
    }
}
