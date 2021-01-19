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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JapaneseHolidays {
    private final 日本の祝休日 holidays = new 日本の祝休日();

    public JapaneseHolidays() {
    }

    /**
     * Add logic based holiday.
     *
     * @param logic ロジック
     */
    public JapaneseHolidays addHoliday(Function<LocalDate, String> logic) {
        holidays.add祝休日(logic);
        return this;
    }

    /**
     * Add fixed holiday
     *
     * @param holiday Holiday
     */
    public JapaneseHolidays addHoliday(Holiday holiday) {
        holidays.add祝休日(new 祝休日(holiday.date, holiday.name));
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
     * Test if the specified date is a business day
     *
     * @param date date
     * @return true if the specified date is a business day
     */
    public boolean isBusinessDay(LocalDate date) {
        return holidays.is営業日(date);
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
     * Returns a business day by a specific date
     *
     * @param date specific date
     * @return last business day by the specified date
     */
    public LocalDate lastBusinessDay(LocalDate date) {
        return holidays.以前の営業日(date);
    }

    /**
     * Returns a business day on and after a specific date
     *
     * @param date specific date
     * @return first business day on and after the specified date
     */
    public LocalDate firstBusinessDay(LocalDate date) {
        return holidays.以降の営業日(date);
    }

    /**
     * Returns a holiday by a specific date
     *
     * @param date specific date
     * @return last holiday by the specified date
     */
    public Holiday lastHoliday(LocalDate date) {
        return new Holiday(holidays.以前の祝休日(date));
    }

    /**
     * Returns a holiday on or after a specific date
     *
     * @param date specific date
     * @return first holiday on or after the specified date
     */
    public Holiday firstHoliday(LocalDate date) {
        return new Holiday(holidays.以降の祝休日(date));
    }
    /**
     * Returns holidays between specified period
     *
     * @param from from date (inclusive)
     * @param to to date (inclusive)
     * @return List of holidays between the specified period
     */
    public List<Holiday> getHolidaysBetween️(LocalDate from, LocalDate to) {
        return holidays.get指定期間内の祝休日️(from, to).stream().map(Holiday::new).collect(Collectors.toList());
    }
}
