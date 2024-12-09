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
package one.cafebabe.businesscalendar4j.exmaple;

import one.cafebabe.businesscalendar4j.BusinessCalendar;
import one.cafebabe.businesscalendar4j.Holiday;

import java.time.LocalDate;
import java.util.Locale;

import static one.cafebabe.businesscalendar4j.BusinessCalendar.CLOSED_ON_SATURDAYS_AND_SUNDAYS;
import static one.cafebabe.businesscalendar4j.BusinessCalendar.JAPAN;

public class JapaneseHolidaysExample {
    public static void main(String[] args) {
        BusinessCalendar businessCalendar = BusinessCalendar.newBuilder().holiday(JAPAN.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH).build();

        // prints true, because it's New Year's Day
        System.out.println("Is Jan 1, 2021 a holiday? "
                + businessCalendar.isHoliday(LocalDate.of(2021, 1, 1)));
        // prints false, because it's New Year's Day
        System.out.println("Is Jan 1, 2021 a business day？: "
                + businessCalendar.isBusinessDay(LocalDate.of(2021, 1, 1)));

        // Gets Coming-of-Age Day
        Holiday holiday = businessCalendar.getHoliday(LocalDate.of(2021, 1, 11));
        System.out.println("What day is January 11, 2021?: " + holiday);

        System.out.println("List of holidays in May 2021: ");
        // 2021-05-03:Constitution Memorial Day、2021-05-04:Greenery day、2021-05-05:Children's day を表示
        businessCalendar.getHolidaysBetween(LocalDate.of(2021, 5, 1)
                , LocalDate.of(2021, 5, 31))
                .forEach(e -> System.out.println(e.date() + ": " + e.name()));

        // Sets custom holidays
        businessCalendar = BusinessCalendar.newBuilder()
                .locale(Locale.ENGLISH)
                .holiday(JAPAN.PUBLIC_HOLIDAYS)
                .holiday(CLOSED_ON_SATURDAYS_AND_SUNDAYS)
                .on(1995, 5, 23).holiday("Java public debut")
                .on(5, 19).holiday("James Gosling's birthday")
                .build();

        // Gets the last business day in January 2021
        System.out.println("Last business day in January 2021: "
                + businessCalendar.lastBusinessDay(LocalDate.of(2021, 1, 31)));
        // Gets the first business day on and after Dec 31 2020
        System.out.println("First business day on and after Dec 31 2020: "
                + businessCalendar.firstBusinessDay(LocalDate.of(2020, 12, 31)));
        // Gets the first holiday on and after February 22, 2021
        System.out.println(businessCalendar.firstHoliday(LocalDate.of(2021, 2, 22)));
        // Gets the last holiday by February 26, 2021
        System.out.println(businessCalendar.lastHoliday(LocalDate.of(2021, 2, 26)));
    }
}
