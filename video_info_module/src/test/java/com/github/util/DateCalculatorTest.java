package com.github.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DateCalculatorTest {

    @Test
    public void testGetMonSun() {
        LocalDate[] monSun = DateCalculator.getMonSun();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        LocalDate expectedMonday = today.with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));
        LocalDate expectedSunday = expectedMonday.plusDays(6);

        assertEquals(expectedMonday, monSun[0], "월요일이 정확하지 않습니다.");
        assertEquals(expectedSunday, monSun[1], "일요일이 정확하지 않습니다.");
    }

    @Test
    public void testGetFirstAndLastDayOfMonth() {
        LocalDate[] firstLastDayOfMonth = DateCalculator.getFirstAndLastDayOfMonth();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate expectedFirstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate expectedLastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        assertEquals(expectedFirstDayOfMonth, firstLastDayOfMonth[0], "달의 첫째 날이 정확하지 않습니다.");
        assertEquals(expectedLastDayOfMonth, firstLastDayOfMonth[1], "달의 마지막 날이 정확하지 않습니다.");
    }

}
