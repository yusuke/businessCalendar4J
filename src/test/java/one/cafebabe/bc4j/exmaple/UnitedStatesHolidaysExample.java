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
package one.cafebabe.bc4j.exmaple;

import one.cafebabe.bc4j.BusinessCalendar;

import java.time.LocalDate;

import static one.cafebabe.bc4j.BusinessCalendar.CLOSED_ON_SATURDAYS_AND_SUNDAYS;
import static one.cafebabe.bc4j.BusinessCalendar.UNITED_STATES;

/**
 * Example
 */
public class UnitedStatesHolidaysExample {
    /**
     * @param args args
     */
    public static void main(String[] args) {
        BusinessCalendar calendar = BusinessCalendar.newBuilder()
                .holiday(UNITED_STATES.PUBLIC_HOLIDAYS)
                .build();

        // prints true, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a holiday?: "
                + calendar.isHoliday(LocalDate.of(2021, 1, 1)));
        // prints false, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a business day?: "
                + calendar.isBusinessDay(LocalDate.of(2021, 1, 1)));

        // get Martin Luther King Jr. Day
        System.out.println("What is Jan, 18 2021?: "
                + calendar.getHoliday(LocalDate.of(2021, 1, 18)));

        System.out.println("List of holidays in 2021: ");
        calendar.getHolidaysBetween(LocalDate.of(2021, 1, 1)
                , LocalDate.of(2021, 12, 31))
                .forEach(e -> System.out.println(e.date + ": " + e.name));

        // sets a fixed custom Holiday
        BusinessCalendar customCalendar = BusinessCalendar.newBuilder()
                .holiday(UNITED_STATES.NEW_YEARS_DAY,
                        UNITED_STATES.MARTIN_LUTHER_KING_JR_DAY,
                        UNITED_STATES.MEMORIAL_DAY,
                        UNITED_STATES.INDEPENDENCE_DAY,
                        UNITED_STATES.LABOR_DAY,
                        UNITED_STATES.VETERANS_DAY,
                        UNITED_STATES.THANKSGIVING_DAY,
                        UNITED_STATES.CHRISTMAS_DAY,
                        CLOSED_ON_SATURDAYS_AND_SUNDAYS)
                .on(5, 19).holiday("James Gosling's birthday")
                .on(1995, 5, 23).holiday("Java public debut")
                .build();

        // Gets the last business day of Jan, 2021 → the answer is Jan 29 since Jan 30, 31 are weekend
        System.out.println("Last business day of Jan 2021: "
                + customCalendar.lastBusinessDay(LocalDate.of(2021, 1, 31)));
        // Gets the first business day on and after July 4, 2021
        // The answer is July 6, 2021 because July 4 and 5 are the Independence day and it's substitute
        System.out.println("First business day on or after July 4, 2021: "
                + customCalendar.firstBusinessDay(LocalDate.of(2021, 7, 4)));
        // First holiday on and after Dec 20, 2021 →  Dec 24 (Christmas Day)
        System.out.println(customCalendar.firstHoliday(LocalDate.of(2021, 12, 20)));
        // Last holiday by Nov 12, 2021 →  Nov 11 (Veterans Day)
        System.out.println(customCalendar.lastHoliday(LocalDate.of(2021, 11, 12)));
    }
}
