/*
   Copyright 2021 the original author or authors.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.samuraism.bc4j;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"ConstantConditions"})
class BusinessCalendarTest {
    @Test
    void isHoliday() {
        BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).build();
        assertAll(
                // New Year's Day
                () -> assertTrue(calendar.isHoliday(LocalDate.of(2021, 1, 1))),
                // normal business day
                () -> assertFalse(calendar.isHoliday(LocalDate.of(2021, 1, 2))),
                // Coming of age day
                () -> assertTrue(calendar.isHoliday(LocalDate.of(2021, 1, 11))),
                // Labor Thanksgiving Day
                () -> assertTrue(calendar.isHoliday(LocalDate.of(2021, 11, 23))),
                // normal business day
                () -> assertFalse(calendar.isHoliday(LocalDate.of(2021, 12, 31)))

        );
    }

    @Test
    void isBusinessDay() {
        BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).build();
        assertAll(
                // New Year's Day
                () -> assertFalse(calendar.isBusinessDay(LocalDate.of(2021, 1, 1))),
                // normal business day
                () -> assertTrue(calendar.isBusinessDay(LocalDate.of(2021, 1, 2))),
                // Coming of age day
                () -> assertFalse(calendar.isBusinessDay(LocalDate.of(2021, 1, 11))),
                // Labor Thanksgiving Day
                () -> assertFalse(calendar.isBusinessDay(LocalDate.of(2021, 11, 23))),
                // normal business day
                () -> assertTrue(calendar.isBusinessDay(LocalDate.of(2021, 12, 31)))

        );
    }

    @Test
    void holidayName() {
        BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH).build();
        assertNull(calendar.getHoliday(LocalDate.of(1954, 1, 15)));
        assertEquals("New Year's Day", calendar.getHoliday(LocalDate.of(1955, 1, 1)).name);
        assertEquals("Coming of age day", calendar.getHoliday(LocalDate.of(2021, 1, 11)).name);
        assertNull(calendar.getHoliday(LocalDate.of(2021, 1, 13)));
        assertEquals("Labor Thanksgiving Day", calendar.getHoliday(LocalDate.of(2021, 11, 23)).name);
    }

    @Test
    void customHoliday() {
        BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH)
                .holiday(LocalDate.of(1977, 6, 17), "just holiday").build();
        assertTrue(calendar.isHoliday(LocalDate.of(1977, 6, 17)));
        assertEquals("just holiday", calendar.getHoliday(LocalDate.of(1977, 6, 17)).name);
    }

    @Test
    void logicBasedHoliday() {
        BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH)
                .holiday(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "Saturday" : null)
                .holiday(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "Sunday" : null).build();
        assertTrue(calendar.isHoliday(LocalDate.of(2021, 1, 23)));
        assertTrue(calendar.isHoliday(LocalDate.of(2021, 1, 24)));
        assertEquals("Saturday", calendar.getHoliday(LocalDate.of(2021, 1, 23)).name);
        assertEquals("Sunday", calendar.getHoliday(LocalDate.of(2021, 1, 24)).name);
        assertEquals("Saturday", calendar.getHoliday(LocalDate.of(2022, 8, 27)).name);
        assertEquals("Sunday", calendar.getHoliday(LocalDate.of(2022, 8, 28)).name);

        calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH)
                .holiday(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "Saturday" : null)
                .holiday(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "Sunday" : null)
                .holiday(e -> e.getMonthValue() == 6 && e.getDayOfMonth() == 17 ? "Somebody's birthday" : null)
                .build();
        assertTrue(calendar.isHoliday(LocalDate.of(2011, 6, 17)));
        assertEquals("Somebody's birthday", calendar.getHoliday(LocalDate.of(2021, 6, 17)).name);
    }

    @Test
    void getHolidaysBetween️() {
        assertAll(
                () -> {
                    BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH).build();
                    final List<Holiday> HolidayList = calendar.getHolidaysBetween️(LocalDate.of(1955, 1, 1),
                            LocalDate.of(1955, 1, 16));
                    assertEquals(2, HolidayList.size());
                    assertEquals("New Year's Day", HolidayList.get(0).name);
                    assertEquals("Coming of age day", HolidayList.get(1).name);
                },
                () -> {
                    BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH).build();
                    // from / to will be flipped if necessary
                    final List<Holiday> HolidayList = calendar.getHolidaysBetween️(LocalDate.of(1955, 1, 16),
                            LocalDate.of(1954, 12, 31));
                    assertEquals(2, HolidayList.size());
                    assertEquals("New Year's Day", HolidayList.get(0).name);
                    assertEquals("Coming of age day", HolidayList.get(1).name);
                },
                () -> {
                    BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH).build();
                    final List<Holiday> HolidayList = calendar.getHolidaysBetween️(LocalDate.of(1955, 1, 1),
                            LocalDate.of(2021, 12, 31));
                    assertEquals(959, HolidayList.size());
                    assertEquals("New Year's Day", HolidayList.get(0).name);
                    assertEquals("Labor Thanksgiving Day", HolidayList.get(958).name);
                },
                () -> {
                    // custom holidays
                    BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH)
                            .holiday(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "Saturday" : null)
                            .holiday(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "Sunday" : null)
                            .build();
                    final List<Holiday> HolidayList = calendar.getHolidaysBetween️(LocalDate.of(1955, 1, 1),
                            LocalDate.of(1955, 1, 16));
                    assertEquals(6, HolidayList.size());
                    assertEquals("New Year's Day", HolidayList.get(0).name);
                    assertEquals("Sunday", HolidayList.get(1).name);
                    assertEquals("Saturday", HolidayList.get(2).name);
                    assertEquals("Sunday", HolidayList.get(3).name);
                    // original holidays are prioritized
                    assertEquals("Coming of age day", HolidayList.get(4).name);
                    assertEquals("Sunday", HolidayList.get(5).name);
                },
                () -> {
                    // no holidays during the specified period
                    BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH).build();
                    final List<Holiday> HolidayList = calendar.getHolidaysBetween️(LocalDate.of(2021, 1, 2),
                            LocalDate.of(2021, 1, 2));
                    assertEquals(0, HolidayList.size());
                });
    }

    @Test
    void lastFirstBusinessDay() {
        BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH)
                .holiday(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "Saturday" : null)
                .holiday(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "Sunday" : null)
                .build();
        assertAll(
                // during holiday
                () -> assertEquals(LocalDate.of(2020, 12, 31),
                        calendar.lastBusinessDay(LocalDate.of(2021, 1, 2))),
                () -> assertEquals(LocalDate.of(2021, 1, 4),
                        calendar.firstBusinessDay(LocalDate.of(2021, 1, 2))),
                // during business day
                () -> assertEquals(LocalDate.of(2021, 1, 6),
                        calendar.lastBusinessDay(LocalDate.of(2021, 1, 6))),
                () -> assertEquals(LocalDate.of(2021, 1, 6),
                        calendar.firstBusinessDay(LocalDate.of(2021, 1, 6))));
    }

    @Test
    void lastFirstHoliday() {
        BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH)
                .holiday(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "Saturday" : null)
                .holiday(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "Sunday" : null)
                .build();
        assertAll(
                // during holiday
                () -> assertEquals(LocalDate.of(2021, 1, 2),
                        calendar.lastHoliday(LocalDate.of(2021, 1, 2)).date),
                () -> assertEquals(LocalDate.of(2021, 1, 2),
                        calendar.firstHoliday(LocalDate.of(2021, 1, 2)).date),
                // during business day
                () -> assertEquals(LocalDate.of(2021, 1, 3),
                        calendar.lastHoliday(LocalDate.of(2021, 1, 6)).date),
                () -> assertEquals(LocalDate.of(2021, 1, 9),
                        calendar.firstHoliday(LocalDate.of(2021, 1, 6)).date)
        );
    }

    @Test
    void outOfScope() {
        BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH).build();
        assertAll(
                // 内閣府でとれるデータの範囲より前
                () -> assertEquals(LocalDate.of(1954, 1, 1),
                        calendar.lastHoliday(LocalDate.of(1954, 6, 17)).date),
                // 内閣府でとれるデータの範囲より後
                () -> assertEquals(LocalDate.of(2051, 1, 1),
                        calendar.firstHoliday(LocalDate.of(2050, 12, 31)).date)
        );
    }

    @Test
    void getCabinetOfficialHolidayDataFirstLastDay() {
        BusinessCalendar calendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH).build();
        assertEquals(LocalDate.of(1955, 1, 1), Japan.getCabinetOfficialHolidayDataFirstDay());
        assertEquals(LocalDate.of(LocalDate.now().getYear() + 1, 11, 23), Japan.getCabinetOfficialHolidayDataLastDay());
        if (LocalDate.now().isAfter(LocalDate.of(2021, 12, 10))) {
            fail("2021年12月には公式の祝休日情報は更新されており2021年11月23日以降の祝休日情報がとれるはず");
        }
    }

    @Test
    void newYearHolidays() {
        assertNull(Japan.CLOSED_ON_NEW_YEARS_HOLIDAYS.apply(LocalDate.of(2020, 12, 31)));
        assertNotNull(Japan.CLOSED_ON_NEW_YEARS_HOLIDAYS.apply(LocalDate.of(2021, 1, 1)));
        assertNotNull(Japan.CLOSED_ON_NEW_YEARS_HOLIDAYS.apply(LocalDate.of(2021, 1, 2)));
        assertNotNull(Japan.CLOSED_ON_NEW_YEARS_HOLIDAYS.apply(LocalDate.of(2022, 1, 3)));
        assertNull(Japan.CLOSED_ON_NEW_YEARS_HOLIDAYS.apply(LocalDate.of(2022, 1, 4)));
    }

    @Test
    void newYearsEve() {
        assertNull(Japan.CLOSED_ON_NEW_YEARS_EVE.apply(LocalDate.of(2021, 12, 30)));
        assertNotNull(Japan.CLOSED_ON_NEW_YEARS_EVE.apply(LocalDate.of(2021, 12, 31)));
        assertNull(Japan.CLOSED_ON_NEW_YEARS_EVE.apply(LocalDate.of(2022, 1, 1)));
    }

    @Test
    void saturdaysSunday() {
        assertNull(BusinessCalendar.CLOSED_ON_SATURDAYS_AND_SUNDAYS.apply(LocalDate.of(2021, 1, 1)));
        assertNotNull(BusinessCalendar.CLOSED_ON_SATURDAYS_AND_SUNDAYS.apply(LocalDate.of(2021, 1, 2)));
        assertNotNull(BusinessCalendar.CLOSED_ON_SATURDAYS_AND_SUNDAYS.apply(LocalDate.of(2021, 1, 3)));
        assertNull(BusinessCalendar.CLOSED_ON_SATURDAYS_AND_SUNDAYS.apply(LocalDate.of(2021, 1, 4)));
    }
}