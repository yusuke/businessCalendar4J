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
package com.samuraism;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"NonAsciiCharacters", "OptionalGetWithoutIsPresent"})
class 日本の祝休日Test {
    @Test
    void is祝日() {
        日本の祝休日 holidays = new 日本の祝休日();
        assertAll(
                // 元日
                () -> assertTrue(holidays.is祝休日(LocalDate.of(2021, 1, 1))),
                // 普通の日
                () -> assertFalse(holidays.is祝休日(LocalDate.of(2021, 1, 2))),
                // 成人の日
                () -> assertTrue(holidays.is祝休日(LocalDate.of(2021, 1, 11))),
                // 勤労感謝の日
                () -> assertTrue(holidays.is祝休日(LocalDate.of(2021, 11, 23))),
                // 大晦日は普通の日
                () -> assertFalse(holidays.is祝休日(LocalDate.of(2021, 12, 31)))

                );
    }

    @Test
    void is営業日() {
        日本の祝休日 holidays = new 日本の祝休日();
        assertAll(
                // 元日
                () -> assertFalse(holidays.is営業日(LocalDate.of(2021, 1, 1))),
                // 普通の日
                () -> assertTrue(holidays.is営業日(LocalDate.of(2021, 1, 2))),
                // 成人の日
                () -> assertFalse(holidays.is営業日(LocalDate.of(2021, 1, 11))),
                // 勤労感謝の日
                () -> assertFalse(holidays.is営業日(LocalDate.of(2021, 11, 23))),
                // 大晦日も営業中
                () -> assertTrue(holidays.is営業日(LocalDate.of(2021, 12, 31)))

        );
    }

    @Test
    void get名称() {
        日本の祝休日 holidays = new 日本の祝休日();
        assertFalse(holidays.get祝休日(LocalDate.of(1954, 1, 1)).isPresent());
        assertEquals("元日", holidays.get祝休日(LocalDate.of(1955, 1, 1)).get().名称);
        assertEquals("成人の日", holidays.get祝休日(LocalDate.of(2021, 1, 11)).get().名称);
        assertFalse(holidays.get祝休日(LocalDate.of(2021, 1, 13)).isPresent());
        assertEquals("勤労感謝の日", holidays.get祝休日(LocalDate.of(2021, 11, 23)).get().名称);
    }

    @Test
    void add祝休日() {
        日本の祝休日 holidays = new 日本の祝休日()
                .add祝休日(new 祝休日(LocalDate.of(1977, 6, 17), "休みたいから休む"));
        assertTrue(holidays.is祝休日(LocalDate.of(1977, 6, 17)));
        assertEquals("休みたいから休む", holidays.get祝休日(LocalDate.of(1977, 6, 17)).get().名称);
    }

    @Test
    void add祝休日ロジックベース() {
        日本の祝休日 holidays = new 日本の祝休日()
                .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null)
                .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null);
        assertTrue(holidays.is祝休日(LocalDate.of(2021, 1, 23)));
        assertTrue(holidays.is祝休日(LocalDate.of(2021, 1, 24)));
        assertEquals("土曜日", holidays.get祝休日(LocalDate.of(2021, 1, 23)).get().名称);
        assertEquals("日曜日", holidays.get祝休日(LocalDate.of(2021, 1, 24)).get().名称);
        assertEquals("土曜日", holidays.get祝休日(LocalDate.of(2022, 8, 27)).get().名称);
        assertEquals("日曜日", holidays.get祝休日(LocalDate.of(2022, 8, 28)).get().名称);

        holidays.add祝休日(e -> e.getMonthValue() == 6 && e.getDayOfMonth() == 17 ? "山本裕介誕生日" : null);
        assertTrue(holidays.is祝休日(LocalDate.of(2011, 6, 17)));
        assertEquals("山本裕介誕生日", holidays.get祝休日(LocalDate.of(2021, 6, 17)).get().名称);
    }

    @Test
    void get指定期間内の祝休日️() {
        assertAll(
                () -> {
                    日本の祝休日 holidays = new 日本の祝休日();
                    final List<祝休日> 祝休日List = holidays.get指定期間内の祝休日️(LocalDate.of(1955, 1, 1),
                            LocalDate.of(1955, 1, 16));
                    assertEquals(2, 祝休日List.size());
                    assertEquals("元日", 祝休日List.get(0).名称);
                    assertEquals("成人の日", 祝休日List.get(1).名称);
                },
                () -> {
                    日本の祝休日 holidays = new 日本の祝休日();
                    // from to が逆でも取得できて、順序は古い日付→新しい日付
                    final List<祝休日> 祝休日List = holidays.get指定期間内の祝休日️(LocalDate.of(1955, 1, 16),
                            LocalDate.of(1954, 12, 31));
                    assertEquals(2, 祝休日List.size());
                    assertEquals("元日", 祝休日List.get(0).名称);
                    assertEquals("成人の日", 祝休日List.get(1).名称);
                },
                () -> {
                    // 全期間
                    日本の祝休日 holidays = new 日本の祝休日();
                    final List<祝休日> 祝休日List = holidays.get指定期間内の祝休日️(LocalDate.of(1940, 1, 1),
                            LocalDate.of(2021, 12, 31));
                    assertEquals(959, 祝休日List.size());
                    assertEquals("元日", 祝休日List.get(0).名称);
                    assertEquals("勤労感謝の日", 祝休日List.get(958).名称);
                },
                () -> {
                    // カスタム祝休日を追加
                    日本の祝休日 holidays = new 日本の祝休日();
                    holidays.add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null);
                    holidays.add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null);
                    final List<祝休日> 祝休日List = holidays.get指定期間内の祝休日️(LocalDate.of(1955, 1, 1),
                            LocalDate.of(1955, 1, 16));
                    assertEquals(6, 祝休日List.size());
                    assertEquals("元日", 祝休日List.get(0).名称);
                    assertEquals("日曜日", 祝休日List.get(1).名称);
                    assertEquals("土曜日", 祝休日List.get(2).名称);
                    assertEquals("日曜日", 祝休日List.get(3).名称);
                    // カスタム祝休日よりもオリジナルが優先される
                    assertEquals("成人の日", 祝休日List.get(4).名称);
                    assertEquals("日曜日", 祝休日List.get(5).名称);
                },
                () -> {
                    // 指定期間に祝休日がない
                    日本の祝休日 holidays = new 日本の祝休日();
                    final List<祝休日> 祝休日List = holidays.get指定期間内の祝休日️(LocalDate.of(2021, 1, 2),
                            LocalDate.of(2021, 1, 2));
                    assertEquals(0, 祝休日List.size());
                });
    }

    @Test
    void 前後の営業日() {
        日本の祝休日 holidays = new 日本の祝休日()
                .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null)
                .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null);
        assertAll(
                // 祝休日中
                () -> assertEquals(LocalDate.of(2020, 12, 31),
                        holidays.以前の営業日(LocalDate.of(2021, 1, 2))),
                () -> assertEquals(LocalDate.of(2021, 1, 4),
                        holidays.以降の営業日(LocalDate.of(2021, 1, 2))),
                // 営業日中
                () -> assertEquals(LocalDate.of(2021, 1, 6),
                        holidays.以前の営業日(LocalDate.of(2021, 1, 6))),
                () -> assertEquals(LocalDate.of(2021, 1, 6),
                        holidays.以降の営業日(LocalDate.of(2021, 1, 6))));
    }

    @Test
    void 前後の祝休日() {
        日本の祝休日 holidays = new 日本の祝休日()
                .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null)
                .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null);
        assertAll(
                // 祝休日中
                () -> assertEquals(LocalDate.of(2021, 1, 2),
                        holidays.以前の祝休日(LocalDate.of(2021, 1, 2)).日付),
                () -> assertEquals(LocalDate.of(2021, 1, 2),
                        holidays.以降の祝休日(LocalDate.of(2021, 1, 2)).日付),
                // 営業日中
                () -> assertEquals(LocalDate.of(2021, 1, 3),
                        holidays.以前の祝休日(LocalDate.of(2021, 1, 6)).日付),
                () -> assertEquals(LocalDate.of(2021, 1, 9),
                        holidays.以降の祝休日(LocalDate.of(2021, 1, 6)).日付)
        );
    }
}