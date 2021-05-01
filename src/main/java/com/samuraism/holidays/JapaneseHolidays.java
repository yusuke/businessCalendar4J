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
import java.util.*;
import java.util.function.Function;

public final class JapaneseHolidays {
    private final List<Function<LocalDate, String>> holidayLogics = new ArrayList<>();
    private final HolidayMap customHolidayMap = new HolidayMap();

    /**
     * Fixed algorithm to close from New Year's Day to Jan 3.
     *
     * @since 1.5
     */
    public static final Function<LocalDate, String> CLOSED_ON_NEW_YEARS_HOLIDAYS = e -> e.getMonthValue() == 1 && e.getDayOfMonth() <= 3 ? "三が日" : null;

    class Localized正月三が日休業 implements Function<LocalDate, String> {
        @Override
        public String apply(LocalDate e) {
            return e.getMonthValue() == 1 && e.getDayOfMonth() <= 3 ? resource.getString("三が日") : null;
        }
    }

    /**
     * Fixed algorithm to close on New Year's Eve.
     *
     * @since 1.5
     */
    public static final Function<LocalDate, String> CLOSED_ON_NEW_YEARS_EVE = e -> e.getMonthValue() == 12 && e.getDayOfMonth() == 31 ? "大晦日" : null;

    class Localized大晦日休業 implements Function<LocalDate, String> {
        @Override
        public String apply(LocalDate e) {
            return e.getMonthValue() == 12 && e.getDayOfMonth() == 31 ? resource.getString("大晦日") : null;
        }
    }

    final JapaneseHolidayAlgorithm holidayAlgorithm;
    final ResourceBundle resource;

    public JapaneseHolidays() {
        this(Locale.getDefault());
    }

    public JapaneseHolidays(Locale locale) {
        resource = ResourceBundle.getBundle("holidays", locale, new ResourceBundle.Control() {
            @Override
            public Locale getFallbackLocale(String baseName, Locale locale) {
                return Locale.ENGLISH;
            }
        });
        holidayAlgorithm = new JapaneseHolidayAlgorithm(resource);
        holidayLogics.add(holidayAlgorithm);
        holidayLogics.add(customHolidayMap);
    }


    /**
     * Fixed algorithm to close on Saturdays and Sundays
     * @since 1.5
     */
    public static final Function<LocalDate, String> CLOSED_ON_SATURDAYS_AND_SUNDAYS = localDate -> {
        switch(localDate.getDayOfWeek()) {
            case SATURDAY:
                return "土曜日";
            case SUNDAY:
                return "日曜日";
            default:
                return null;
        }
    };

    class Localized土日休業 implements Function<LocalDate, String> {
        @Override
        public String apply(LocalDate localDate) {
            switch(localDate.getDayOfWeek()) {
                case SATURDAY:
                    return resource.getString("土曜日");
                case SUNDAY:
                    return resource.getString("日曜日");
                default:
                    return null;
            }
        }
    }

    /**
     * Add logic based holiday.
     *
     * @param logic ロジック
     * @return このインスタンス
     */
    public JapaneseHolidays addHoliday(Function<LocalDate, String> logic) {
        if(logic == CLOSED_ON_SATURDAYS_AND_SUNDAYS){
            logic = new Localized土日休業();
        }
        if(logic == CLOSED_ON_NEW_YEARS_HOLIDAYS){
            logic = new Localized正月三が日休業();
        }
        if(logic == CLOSED_ON_NEW_YEARS_EVE){
            logic = new Localized大晦日休業();
        }
        holidayLogics.add(logic);
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

    /**
     * Returns the first day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>.
     * @return the first day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>
     * @since 1.4
     */
    public LocalDate getCabinetOfficialHolidayDataFirstDay() {
        return holidayAlgorithm.csv.holidayMap.firstKey();
    }

    /**
     * Returns the last day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>.
     * @return the last day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>
     * @since 1.4
     */
    public LocalDate getCabinetOfficialHolidayDataLastDay() {
        return holidayAlgorithm.csv.holidayMap.lastKey();
    }
}
