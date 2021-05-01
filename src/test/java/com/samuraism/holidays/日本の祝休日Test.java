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
package com.samuraism.holidays;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class 日本の祝休日Test {
    @Test
    void is祝日() {
        JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPAN);
        assertAll(
                // 元日
                () -> assertTrue(holidays.isHoliday(LocalDate.of(2021, 1, 1))),
                // 普通の日
                () -> assertFalse(holidays.isHoliday(LocalDate.of(2021, 1, 2))),
                // 成人の日
                () -> assertTrue(holidays.isHoliday(LocalDate.of(2021, 1, 11))),
                // 勤労感謝の日
                () -> assertTrue(holidays.isHoliday(LocalDate.of(2021, 11, 23))),
                // 大晦日は普通の日
                () -> assertFalse(holidays.isHoliday(LocalDate.of(2021, 12, 31)))

        );
    }

    @Test
    void is営業日() {
        JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE);
        assertAll(
                // 元日
                () -> assertFalse(holidays.isBusinessDay(LocalDate.of(2021, 1, 1))),
                // 普通の日
                () -> assertTrue(holidays.isBusinessDay(LocalDate.of(2021, 1, 2))),
                // 成人の日
                () -> assertFalse(holidays.isBusinessDay(LocalDate.of(2021, 1, 11))),
                // 勤労感謝の日
                () -> assertFalse(holidays.isBusinessDay(LocalDate.of(2021, 11, 23))),
                // 大晦日も営業中
                () -> assertTrue(holidays.isBusinessDay(LocalDate.of(2021, 12, 31)))

        );
    }

    @Test
    void get名称() {
        JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE);
        assertFalse(holidays.getHoliday(LocalDate.of(1954, 1, 15)).isPresent());
        assertEquals("元日", holidays.getHoliday(LocalDate.of(1955, 1, 1)).get().name);
        assertEquals("成人の日", holidays.getHoliday(LocalDate.of(2021, 1, 11)).get().name);
        assertFalse(holidays.getHoliday(LocalDate.of(2021, 1, 13)).isPresent());
        assertEquals("勤労感謝の日", holidays.getHoliday(LocalDate.of(2021, 11, 23)).get().name);
    }

    @Test
    void add祝休日() {
        Holidays holidays = new JapaneseHolidays(Locale.JAPANESE)
                .addHoliday(LocalDate.of(1977, 6, 17), "休みたいから休む");
        assertTrue(holidays.isHoliday(LocalDate.of(1977, 6, 17)));
        assertEquals("休みたいから休む", holidays.getHoliday(LocalDate.of(1977, 6, 17)).get().name);
    }

    @Test
    void add祝休日ロジックベース() {
        JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE)
                .addHoliday(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null)
                .addHoliday(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null);
        assertTrue(holidays.isHoliday(LocalDate.of(2021, 1, 23)));
        assertTrue(holidays.isHoliday(LocalDate.of(2021, 1, 24)));
        assertEquals("土曜日", holidays.getHoliday(LocalDate.of(2021, 1, 23)).get().name);
        assertEquals("日曜日", holidays.getHoliday(LocalDate.of(2021, 1, 24)).get().name);
        assertEquals("土曜日", holidays.getHoliday(LocalDate.of(2022, 8, 27)).get().name);
        assertEquals("日曜日", holidays.getHoliday(LocalDate.of(2022, 8, 28)).get().name);

        holidays.addHoliday(e -> e.getMonthValue() == 6 && e.getDayOfMonth() == 17 ? "山本裕介誕生日" : null);
        assertTrue(holidays.isHoliday(LocalDate.of(2011, 6, 17)));
        assertEquals("山本裕介誕生日", holidays.getHoliday(LocalDate.of(2021, 6, 17)).get().name);
    }

    @Test
    void get指定期間内の祝休日️() {
        assertAll(
                () -> {
                    JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE);
                    final List<Holiday> HolidayList = holidays.getHolidaysBetween️(LocalDate.of(1955, 1, 1),
                            LocalDate.of(1955, 1, 16));
                    assertEquals(2, HolidayList.size());
                    assertEquals("元日", HolidayList.get(0).name);
                    assertEquals("成人の日", HolidayList.get(1).name);
                },
                () -> {
                    JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE);
                    // from to が逆でも取得できて、順序は古い日付→新しい日付
                    final List<Holiday> HolidayList = holidays.getHolidaysBetween️(LocalDate.of(1955, 1, 16),
                            LocalDate.of(1954, 12, 31));
                    assertEquals(2, HolidayList.size());
                    assertEquals("元日", HolidayList.get(0).name);
                    assertEquals("成人の日", HolidayList.get(1).name);
                },
                () -> {
                    // 全期間
                    JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE);
                    final List<Holiday> HolidayList = holidays.getHolidaysBetween️(LocalDate.of(1955, 1, 1),
                            LocalDate.of(2021, 12, 31));
                    assertEquals(959, HolidayList.size());
                    assertEquals("元日", HolidayList.get(0).name);
                    assertEquals("勤労感謝の日", HolidayList.get(958).name);
                },
                () -> {
                    // カスタム祝休日を追加
                    JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE);
                    holidays.addHoliday(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null);
                    holidays.addHoliday(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null);
                    final List<Holiday> HolidayList = holidays.getHolidaysBetween️(LocalDate.of(1955, 1, 1),
                            LocalDate.of(1955, 1, 16));
                    assertEquals(6, HolidayList.size());
                    assertEquals("元日", HolidayList.get(0).name);
                    assertEquals("日曜日", HolidayList.get(1).name);
                    assertEquals("土曜日", HolidayList.get(2).name);
                    assertEquals("日曜日", HolidayList.get(3).name);
                    // カスタム祝休日よりもオリジナルが優先される
                    assertEquals("成人の日", HolidayList.get(4).name);
                    assertEquals("日曜日", HolidayList.get(5).name);
                },
                () -> {
                    // 指定期間に祝休日がない
                    JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE);
                    final List<Holiday> HolidayList = holidays.getHolidaysBetween️(LocalDate.of(2021, 1, 2),
                            LocalDate.of(2021, 1, 2));
                    assertEquals(0, HolidayList.size());
                });
    }

    @Test
    void 前後の営業日() {
        JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE)
                .addHoliday(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null)
                .addHoliday(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null);
        assertAll(
                // 祝休日中
                () -> assertEquals(LocalDate.of(2020, 12, 31),
                        holidays.lastBusinessDay(LocalDate.of(2021, 1, 2))),
                () -> assertEquals(LocalDate.of(2021, 1, 4),
                        holidays.firstBusinessDay(LocalDate.of(2021, 1, 2))),
                // 営業日中
                () -> assertEquals(LocalDate.of(2021, 1, 6),
                        holidays.lastBusinessDay(LocalDate.of(2021, 1, 6))),
                () -> assertEquals(LocalDate.of(2021, 1, 6),
                        holidays.firstBusinessDay(LocalDate.of(2021, 1, 6))));
    }

    @Test
    void 前後の祝休日() {
        JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE)
                .addHoliday(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null)
                .addHoliday(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null);
        assertAll(
                // 祝休日中
                () -> assertEquals(LocalDate.of(2021, 1, 2),
                        holidays.lastHoliday(LocalDate.of(2021, 1, 2)).date),
                () -> assertEquals(LocalDate.of(2021, 1, 2),
                        holidays.firstHoliday(LocalDate.of(2021, 1, 2)).date),
                // 営業日中
                () -> assertEquals(LocalDate.of(2021, 1, 3),
                        holidays.lastHoliday(LocalDate.of(2021, 1, 6)).date),
                () -> assertEquals(LocalDate.of(2021, 1, 9),
                        holidays.firstHoliday(LocalDate.of(2021, 1, 6)).date)
        );
    }

    @Test
    void 範囲外() {
        JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE);
        assertAll(
                // 内閣府でとれるデータの範囲より前
                () -> assertEquals(LocalDate.of(1954, 1, 1),
                        holidays.lastHoliday(LocalDate.of(1954, 6, 17)).date),
                // 内閣府でとれるデータの範囲より後
                () -> assertEquals(LocalDate.of(2051, 1, 1),
                        holidays.firstHoliday(LocalDate.of(2050, 12, 31)).date)
        );
    }

    @Test
    void get内閣府公式公表期間() {
        JapaneseHolidays holidays = new JapaneseHolidays(Locale.JAPANESE);
        assertEquals(LocalDate.of(1955, 1, 1), holidays.getCabinetOfficialHolidayDataFirstDay());
        assertEquals(LocalDate.of(LocalDate.now().getYear() + 1, 11, 23), holidays.getCabinetOfficialHolidayDataLastDay());
        if (LocalDate.now().isAfter(LocalDate.of(2021, 12, 10))) {
            fail("2021年12月には公式の祝休日情報は更新されており2021年11月23日以降の祝休日情報がとれるはず");
        }
    }

    @Test
    void 正月三が日休業(){
        assertNull(JapaneseHolidays.CLOSED_ON_NEW_YEARS_HOLIDAYS.apply(LocalDate.of(2020, 12, 31)));
        assertNotNull(JapaneseHolidays.CLOSED_ON_NEW_YEARS_HOLIDAYS.apply(LocalDate.of(2021, 1, 1)));
        assertNotNull(JapaneseHolidays.CLOSED_ON_NEW_YEARS_HOLIDAYS.apply(LocalDate.of(2021, 1, 2)));
        assertNotNull(JapaneseHolidays.CLOSED_ON_NEW_YEARS_HOLIDAYS.apply(LocalDate.of(2022, 1, 3)));
        assertNull(JapaneseHolidays.CLOSED_ON_NEW_YEARS_HOLIDAYS.apply(LocalDate.of(2022, 1, 4)));
    }

    @Test
    void 大晦日休業(){
        assertNull(JapaneseHolidays.CLOSED_ON_NEW_YEARS_EVE.apply(LocalDate.of(2021, 12, 30)));
        assertNotNull(JapaneseHolidays.CLOSED_ON_NEW_YEARS_EVE.apply(LocalDate.of(2021, 12, 31)));
        assertNull(JapaneseHolidays.CLOSED_ON_NEW_YEARS_EVE.apply(LocalDate.of(2022, 1, 1)));
    }

    @Test
    void 土日休業(){
        assertNull(JapaneseHolidays.CLOSED_ON_SATURDAYS_AND_SUNDAYS.apply(LocalDate.of(2021, 1, 1)));
        assertNotNull(JapaneseHolidays.CLOSED_ON_SATURDAYS_AND_SUNDAYS.apply(LocalDate.of(2021, 1, 2)));
        assertNotNull(JapaneseHolidays.CLOSED_ON_SATURDAYS_AND_SUNDAYS.apply(LocalDate.of(2021, 1, 3)));
        assertNull(JapaneseHolidays.CLOSED_ON_SATURDAYS_AND_SUNDAYS.apply(LocalDate.of(2021, 1, 4)));
    }
}