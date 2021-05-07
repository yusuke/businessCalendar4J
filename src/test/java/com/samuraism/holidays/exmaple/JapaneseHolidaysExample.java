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
package com.samuraism.holidays.exmaple;

import com.samuraism.holidays.Holiday;
import com.samuraism.holidays.JapaneseHolidays;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

public class JapaneseHolidaysExample {
    public static void main(String[] args) {
        JapaneseHolidays holidays = JapaneseHolidays.newBuilder().locale(Locale.ENGLISH).build();

        // prints true, because it's New Year's Day
        System.out.println("Is Jan 1, 2021 a holiday? "
                + holidays.isHoliday(LocalDate.of(2021, 1, 1)));
        // prints false, because it's New Year's Day
        System.out.println("Is Jan 1, 2021 a business day？: "
                + holidays.isBusinessDay(LocalDate.of(2021, 1, 1)));

        // Gets Coming-of-Age Day
        Optional<Holiday> holiday = holidays.getHoliday(LocalDate.of(2021, 1, 11));
        holiday.ifPresent(e -> System.out.println("What day is January 11, 2021?: " + e.name));

        System.out.println("List of holidays in May 2021: ");
        // 2021-05-03:Constitution Memorial Day、2021-05-04:Greenery day、2021-05-05:Children's day を表示
        holidays.getHolidaysBetween️(LocalDate.of(2021, 5, 1)
                , LocalDate.of(2021, 5, 31))
                .forEach(e -> System.out.println(e.date + ": " + e.name));

        // Sets custom holidays
        holidays = JapaneseHolidays.newBuilder()
                .locale(Locale.ENGLISH)
                .holiday(LocalDate.of(1995, 5, 23), "Java public debut")
                .holiday(JapaneseHolidays.CLOSED_ON_SATURDAYS_AND_SUNDAYS)
                // ロジックベーのカスタム祝休日を設定。当該日が祝日ならば名称を、そうでなければnullを返す関数を指定する
                .holiday(e -> e.getMonthValue() == 5 && e.getDayOfMonth() == 19 ? "James Gosling's birthday" : null)
                .build();

        // Gets the last business day in January 2021
        System.out.println("Last business day in January 2021: "
                + holidays.lastBusinessDay(LocalDate.of(2021, 1, 31)));
        // Gets the first business day on and after Dec 31 2020
        System.out.println("First business day on and after Dec 31 2020: "
                + holidays.firstBusinessDay(LocalDate.of(2020, 12, 31)));
        // Gets the first holiday on and after February 22, 2021
        System.out.println(holidays.firstHoliday(LocalDate.of(2021, 2, 22)));
        // Gets the last holiday by February 26, 2021
        System.out.println(holidays.lastHoliday(LocalDate.of(2021, 2, 26)));
    }
}
