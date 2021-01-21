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
package com.samuraism.holidays.exmaple.en;

import com.samuraism.holidays.en.JapaneseHolidays;
import com.samuraism.holidays.en.Holiday;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

public class Example {
    public static void main(String[] args) {
        JapaneseHolidays holidays = new JapaneseHolidays();

        // prints true, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a holiday?: " + holidays.isHoliday(LocalDate.of(2021, 1, 1)));
        // prints false, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a business day?: " + holidays.isBusinessDay(LocalDate.of(2021, 1, 1)));

        // get 成人の日
        Optional<Holiday> holiday = holidays.getHoliday(LocalDate.of(2021, 1, 11));
        holiday.ifPresent(e -> System.out.println("What is Jan, 11 2021?: " + e.name));

        System.out.println("List of holidays in May 2021: ");
        // shows 2021-05-03:憲法記念日、2021-05-04:みどりの日、2021-05-05:こどもの日
        holidays.getHolidaysBetween(LocalDate.of(2021, 5, 1)
                , LocalDate.of(2021, 5, 31))
                .forEach(e -> System.out.println(e.date + ": " + e.name));

        // sets a fixed custom Holiday
        // You can specify custom holidays using method chain. Note that the JapaneseHolidays instance is mutated upon each method call.
        holidays.addHoliday(LocalDate.of(2013, 3, 29), "Samuraism Inc. Foundation Day")
                // Specify logic based custom holidays. returns a string if the day is a holiday
                .addHoliday(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "Saturday" : null)
                .addHoliday(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "Sunday" : null)
                .addHoliday(e -> e.getMonthValue() == 12 && e.getDayOfMonth() == 31 ? "New Year's Eve" : null);

        // Gets the last business day of Jan, 2021 → the answer is Jan 29 since Jan 30, 31 are weekend
        System.out.println("Last business day of Jan 2021: " + holidays.lastBusinessDay(LocalDate.of(2021, 1, 31)));
        // Gets the first business day on and after New Year's Eve 2020 → the answer is Jan 4 as Jan 1 is New Year's Day, Jan 2,3 are custom holidays
        System.out.println("First business day on or after Dec, 2020: " + holidays.firstBusinessDay(LocalDate.of(2020, 12, 31)));
        // First holiday on and after Feb 22, 2021 →  Feb 23 (Emperor's Birthday)
        System.out.println(holidays.firstHoliday(LocalDate.of(2021, 2, 22)));
        // Last holiday by Feb 26, 2021 →  Feb 23 (Emperor's Birthday)
        System.out.println(holidays.lastHoliday(LocalDate.of(2021, 2, 26)));
    }
}
