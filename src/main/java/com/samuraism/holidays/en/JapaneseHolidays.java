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
package com.samuraism.holidays.en;

import com.samuraism.holidays.日本の祝休日;
import com.samuraism.holidays.祝休日;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JapaneseHolidays {
    private final 日本の祝休日 holidays = new 日本の祝休日();

    public JapaneseHolidays() {
    }

    /**
     * Add logic based holiday.
     *
     * @param logic logic
     * @return This instance
     */
    public JapaneseHolidays addHoliday(Function<LocalDate, String> logic) {
        holidays.add祝休日(logic);
        return this;
    }

    /**
     * Add fixed holiday
     *
     * @param date date
     * @param name name
     * @return This instance
     */
    public JapaneseHolidays addHoliday(LocalDate date, String name) {
        holidays.add祝休日(date, name);
        return this;
    }


    /**
     * Test if the specified date is a holiday
     *
     * @param date date
     * @return true if the specified date is a holiday
     */
    public boolean isHoliday(LocalDate date) {
        return holidays.is祝休日(date);
    }

    /**
     * Test if today is a holiday
     *
     * @return true if the specified date is a holiday
     * @since 1.3
     */
    public boolean isHoliday() {
        return holidays.is祝休日();
    }

    /**
     * Test if the specified date is a business day
     *
     * @param date date
     * @return true if the specified date is a business day
     */
    public boolean isBusinessDay(LocalDate date) {
        return holidays.is営業日(date);
    }

    /**
     * Test if today is a business day
     *
     * @return true if the specified date is a business day
     * @since 1.3
     */
    public boolean isBusinessDay() {
        return holidays.is営業日();
    }

    /**
     * Returns a Holiday on the specified date
     *
     * @param date date
     * @return Holiday
     */
    public Optional<Holiday> getHoliday(LocalDate date) {
        final Optional<祝休日> holiday = holidays.get祝休日(date);
        return holiday.map(Holiday::new);
    }

    /**
     * Returns the last business day by a specific date
     *
     * @param date specific date
     * @return last business day by the specified date
     */
    public LocalDate lastBusinessDay(LocalDate date) {
        return holidays.最後の営業日(date);
    }

    /**
     * Returns the last business day by today
     *
     * @return last business day by the specified date
     * @since 1.4
     */
    public LocalDate lastBusinessDay() {
        return holidays.最後の営業日();
    }

    /**
     * Returns the first business day on or after a specific date
     *
     * @param date specific date
     * @return first business day on and after the specified date
     */
    public LocalDate firstBusinessDay(LocalDate date) {
        return holidays.最初の営業日(date);
    }

    /**
     * Returns the first business day after today
     *
     * @return first business day after today
     * @since 1.4
     */
    public LocalDate firstBusinessDay() {
        return holidays.最初の営業日();
    }

    /**
     * Returns the last holiday by a specific date
     *
     * @param date specific date
     * @return last holiday by the specified date
     */
    public Holiday lastHoliday(LocalDate date) {
        return new Holiday(holidays.最後の祝休日(date));
    }

    /**
     * Returns the last holiday by today
     *
     * @return last holiday by today
     * @since 1.4
     */
    public Holiday lastHoliday() {
        return new Holiday(holidays.最後の祝休日());
    }

    /**
     * Returns the first holiday on or after a specific date
     *
     * @param date specific date
     * @return first holiday on or after the specified date
     */
    public Holiday firstHoliday(LocalDate date) {
        return new Holiday(holidays.最初の祝休日(date));
    }

    /**
     * Returns the first holiday after today
     *
     * @return first holiday on or after the specified date
     */
    public Holiday firstHoliday() {
        return new Holiday(holidays.最初の祝休日());
    }

    /**
     * Returns holidays between specified period
     *
     * @param from from date (inclusive)
     * @param to to date (inclusive)
     * @return List of holidays between the specified period
     */
    public List<Holiday> getHolidaysBetween(LocalDate from, LocalDate to) {
        return holidays.get指定期間内の祝休日️(from, to).stream().map(Holiday::new).collect(Collectors.toList());
    }

    /**
     * Returns the first day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>.
     * @return the first day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>
     * @since 1.4
     */
    public LocalDate getCabinetOfficialHolidayDataFirstDay(){
        return  holidays.get内閣府公表祝休日初日();
    }

    /**
     * Returns the last day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>.
     * @return the last day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>
     * @since 1.4
     */
    public LocalDate getCabinetOfficialHolidayDataLastDay(){
        return holidays.get内閣府公表祝休日最終日();
    }
}
