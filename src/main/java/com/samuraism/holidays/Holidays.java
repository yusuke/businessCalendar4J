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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class Holidays {
    private final List<Function<LocalDate, String>> holidayLogics = new ArrayList<>();
    private final HolidayMap customHolidayMap = new HolidayMap();
    Holidays(){
        holidayLogics.add(customHolidayMap);
    }
    /**
     * Add logic based holiday.
     *
     * @param logic logic
     * @return This instance
     */
    public Holidays addHoliday(Function<LocalDate, String> logic) {
        holidayLogics.add(0, logic);
        return this;
    }

    /**
     * Add fixed holiday
     *
     * @param date date
     * @param name name
     * @return This instance
     */
    public Holidays addHoliday(LocalDate date, String name) {
        customHolidayMap.addHoliday(date, name);
        return this;
    }

    /**
     * Test if the specified date is a holiday
     *
     * @param date date
     * @return true if the specified date is a holiday
     */
    public boolean isHoliday(LocalDate date) {
        return holidayLogics.stream().anyMatch(e -> e.apply(date) != null);
    }

    /**
     * Test if today is a holiday
     *
     * @return true if the specified date is a holiday
     * @since 1.3
     */
    public boolean isHoliday() {
        final LocalDate today = LocalDate.now();
        return holidayLogics.stream().anyMatch(e -> e.apply(today) != null);
    }

    /**
     * Test if the specified date is a business day
     *
     * @param date date
     * @return true if the specified date is a business day
     */
    public boolean isBusinessDay(LocalDate date) {
        return !isHoliday(date);
    }

    /**
     * Test if today is a business day
     *
     * @return true if the specified date is a business day
     * @since 1.3
     */
    public boolean isBusinessDay() {
        return !isHoliday(LocalDate.now());
    }

    /**
     * Returns a Holiday on the specified date
     *
     * @param date date
     * @return Holiday
     */
    public Optional<Holiday> getHoliday(LocalDate date) {
        final Optional<String> first = holidayLogics.stream()
                .map(e -> e.apply(date)).filter(Objects::nonNull).findFirst();
        return first.map(s -> new Holiday(date, s));
    }

    /**
     * Returns the last business day by a specific date
     *
     * @param date specific date
     * @return last business day by the specified date
     */
    public LocalDate lastBusinessDay(LocalDate date) {
        LocalDate check = date;
        while (isHoliday(check)) {
            check = check.minus(1, ChronoUnit.DAYS);
        }
        return check;
    }

    /**
     * Returns the last business day by today
     *
     * @return last business day by the specified date
     * @since 1.4
     */
    public LocalDate lastBusinessDay() {
        return lastBusinessDay(LocalDate.now());
    }

    /**
     * Returns the first business day on or after a specific date
     *
     * @param date specific date
     * @return first business day on and after the specified date
     */
    public LocalDate firstBusinessDay(LocalDate date) {
        LocalDate check = date;
        while (isHoliday(check)) {
            check = check.plus(1, ChronoUnit.DAYS);
        }
        return check;
    }

    /**
     * Returns the first business day after today
     *
     * @return first business day after today
     * @since 1.4
     */
    public LocalDate firstBusinessDay() {
        return firstBusinessDay(LocalDate.now());
    }

    /**
     * Returns the last holiday by a specific date
     *
     * @param date specific date
     * @return last holiday by the specified date
     */
    public Holiday lastHoliday(LocalDate date) {
        LocalDate check = date;
        while (!isHoliday(check)) {
            check = check.minus(1, ChronoUnit.DAYS);
        }
        //noinspection OptionalGetWithoutIsPresent
        return getHoliday(check).get();
    }

    /**
     * Returns the last holiday by today
     *
     * @return last holiday by today
     * @since 1.4
     */
    public Holiday lastHoliday() {
        return lastHoliday(LocalDate.now());
    }

    /**
     * Returns the first holiday on or after a specific date
     *
     * @param date specific date
     * @return first holiday on or after the specified date
     */
    public Holiday firstHoliday(LocalDate date) {
        LocalDate check = date;
        while (!isHoliday(check)) {
            check = check.plus(1, ChronoUnit.DAYS);
        }
        //noinspection OptionalGetWithoutIsPresent
        return getHoliday(check).get();
    }

    /**
     * Returns the first holiday after today
     *
     * @return first holiday on or after the specified date
     */
    public Holiday firstHoliday() {
        return firstHoliday(LocalDate.now());
    }

    /**
     * Returns holidays between specified period
     *
     * @param from from date (inclusive)
     * @param to to date (inclusive)
     * @return List of holidays between the specified period
     */
    public List<Holiday> getHolidaysBetween️(LocalDate from, LocalDate to) {
        List<Holiday> list = new ArrayList<>();
        LocalDate start = from.isBefore(to) ? from : to;
        LocalDate end = (to.isAfter(from) ? to : from).plus(1, ChronoUnit.DAYS);
        while (start.isBefore(end)) {
            final Optional<Holiday> holiday = getHoliday(start);
            holiday.ifPresent(list::add);
            start = start.plus(1, ChronoUnit.DAYS);
        }
        return list;
    }

}
