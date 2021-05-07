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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public abstract class HolidaysBuilder<E extends Holidays> {
    List<Function<LocalDate, String>> holidayLogics = new ArrayList<>();
    HolidayMap customHolidayMap = new HolidayMap();
    Locale locale = Locale.getDefault();

    public final HolidaysBuilder<E> locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Add logic based holiday(s).
     *
     * @param logics logics
     * @return This instance
     */
    @SafeVarargs
    public final HolidaysBuilder<E> holiday(Function<LocalDate, String>... logics) {
        Collections.addAll(holidayLogics, logics);
        return this;
    }

    /**
     * Add fixed holiday
     *
     * @param date date
     * @param name name
     * @return This instance
     */
    public HolidaysBuilder<E> holiday(LocalDate date, String name) {
        customHolidayMap.addHoliday(date, name);
        return this;
    }

    public abstract E build();
}
