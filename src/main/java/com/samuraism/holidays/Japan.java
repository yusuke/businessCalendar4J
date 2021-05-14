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
import java.util.Random;
import java.util.function.Function;

public final class Japan implements Function<LocalDate, String>{

    /**
     * Fixed algorithm to close from New Year's Day to Jan 3.
     *
     * @since 1.5
     */
    public static final Function<LocalDate, String> CLOSED_ON_NEW_YEARS_HOLIDAYS = e -> e.getMonthValue() == 1 && e.getDayOfMonth() <= 3 ? "三が日" : null;

    /**
     * Fixed algorithm to close on New Year's Eve.
     *
     * @since 1.5
     */
    public static final Function<LocalDate, String> CLOSED_ON_NEW_YEARS_EVE = e -> e.getMonthValue() == 12 && e.getDayOfMonth() == 31 ? "大晦日" : null;

    private static final long aboutOneMonth = 1000L * 60 * 60 * 24 * 31 + new Random(System.currentTimeMillis()).nextLong() % (1000L * 60 * 60 * 10);
    static final CSVHolidays csv = new CSVHolidays(aboutOneMonth, System.getProperty("SYUKUJITSU_URL",
                "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv"));

    final JapaneseHolidayAlgorithm holidayAlgorithm;

    private Japan() {
        holidayAlgorithm = new JapaneseHolidayAlgorithm(csv);
    }
    public static final Function<LocalDate, String> PUBLIC_HOLIDAYS = new Japan();

    @Override
    public String apply(LocalDate localDate) {
        String apply = csv.apply(localDate);
        if (apply == null) {
            apply = holidayAlgorithm.apply(localDate);
        }
        return apply;
    }

    /**
     * Returns the first day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>.
     *
     * @return the first day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>
     * @since 1.4
     */
    public static LocalDate getCabinetOfficialHolidayDataFirstDay() {
        return csv.holidayMap.firstKey();
    }

    /**
     * Returns the last day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>.
     *
     * @return the last day of <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">cabinet's official holiday data</a>
     * @since 1.4
     */
    public static LocalDate getCabinetOfficialHolidayDataLastDay() {
        return csv.holidayMap.lastKey();
    }
}
