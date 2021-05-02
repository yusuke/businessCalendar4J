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
import com.samuraism.holidays.UnitedStatesHolidays;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import static com.samuraism.holidays.UnitedStatesHolidays.*;

public class UnitedStatesHolidaysExample {
    public static void main(String[] args) {
        UnitedStatesHolidays holidays = UnitedStatesHolidays.getInstance(e -> e.locale(Locale.ENGLISH)
                .holiday(NEW_YEARS_DAY,
                        MARTIN_LUTHER_KING_JR_DAY,
                        MEMORIAL_DAY,
                        INDEPENDENCE_DAY,
                        LABOR_DAY,
                        VETERANS_DAY,
                        THANKS_GIVING_DAY,
                        CHRISTMAS_DAY));

        // prints true, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a holiday?: " 
                + holidays.isHoliday(LocalDate.of(2021, 1, 1)));
        // prints false, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a business day?: "
                + holidays.isBusinessDay(LocalDate.of(2021, 1, 1)));

        // get Martin Luther King Jr. Day
        Optional<Holiday> holiday = holidays.getHoliday(LocalDate.of(2021, 1, 18));
        holiday.ifPresent(e -> System.out.println("What is Jan, 18 2021?: " + e.name));

        System.out.println("List of holidays in 2021: ");
        holidays.getHolidaysBetween️(LocalDate.of(2021, 1, 1)
                , LocalDate.of(2021, 12, 31))
                .forEach(e -> System.out.println(e.date + ": " + e.name));

        // sets a fixed custom Holiday
        // You can specify custom holidays using method chain. 
        // Note that the UnitedStatesHolidays instance is mutated upon each method call.
        UnitedStatesHolidays customHolidays = UnitedStatesHolidays.getInstance(conf -> conf
                .locale(Locale.ENGLISH)
                .holiday(NEW_YEARS_DAY,
                        MARTIN_LUTHER_KING_JR_DAY,
                        MEMORIAL_DAY,
                        INDEPENDENCE_DAY,
                        LABOR_DAY,
                        VETERANS_DAY,
                        THANKS_GIVING_DAY,
                        CHRISTMAS_DAY,
                        CLOSED_ON_SATURDAYS_AND_SUNDAYS,
                        // Specify logic based custom holidays. returns a string if the day is a holiday
                        e -> e.getMonthValue() == 5 && e.getDayOfMonth() == 19 ? "James Gosling's birthday" : null)
                .holiday(LocalDate.of(1995, 5, 23), "Java public debut"));

        // Gets the last business day of Jan, 2021 → the answer is Jan 29 since Jan 30, 31 are weekend
        System.out.println("Last business day of Jan 2021: " 
                + customHolidays.lastBusinessDay(LocalDate.of(2021, 1, 31)));
        // Gets the first business day on and after July 4, 2021
        // The answer is July 6, 2021 because July 4 and 5 are the Independence day and it's substitute
        System.out.println("First business day on or after July 4, 2021: " 
                + customHolidays.firstBusinessDay(LocalDate.of(2021, 7, 4)));
        // First holiday on and after Dec 20, 2021 →  Dec 24 (Christmas Day)
        System.out.println(customHolidays.firstHoliday(LocalDate.of(2021, 12, 20)));
        // Last holiday by Nov 12, 2021 →  Nov 11 (Veterans Day)
        System.out.println(customHolidays.lastHoliday(LocalDate.of(2021, 11, 12)));
    }
}
