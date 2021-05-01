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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

public final class JapaneseHolidays extends Holidays {

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
        super();
        resource = ResourceBundle.getBundle("holidays", locale, new ResourceBundle.Control() {
            @Override
            public Locale getFallbackLocale(String baseName, Locale locale) {
                return Locale.ENGLISH;
            }
        });
        holidayAlgorithm = new JapaneseHolidayAlgorithm(resource);
        addHoliday(holidayAlgorithm);
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
    @Override
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
        super.addHoliday(logic);
        return this;
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
