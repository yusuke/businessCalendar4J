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

public final class 日本の祝休日Builder {
    final List<Function<LocalDate, String>> holidayLogics = new ArrayList<>();
    final HolidayMap customHolidayMap = new HolidayMap();
    Locale locale = Locale.getDefault();

    日本の祝休日Builder() {
    }

    public final 日本の祝休日Builder locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * ロジックベースの祝休日を追加する.
     *
     * @param logics logics
     * @return このインスタンス
     */
    @SafeVarargs
    public final 日本の祝休日Builder 祝休日(Function<LocalDate, String>... logics) {
        Collections.addAll(holidayLogics, logics);
        return this;
    }

    /**
     * 固定の祝休日を追加する
     *
     * @param 日付 日付
     * @param 名称 名称
     * @return このインスタンス
     */
    public 日本の祝休日Builder 祝休日(LocalDate 日付, String 名称) {
        customHolidayMap.addHoliday(日付, 名称);
        return this;
    }

    public 日本の祝休日 build() {
        final HolidaysBuilder<JapaneseHolidays> builder = JapaneseHolidays.newBuilder();
        builder.locale(this.locale);
        builder.holidayLogics = this.holidayLogics;
        builder.customHolidayMap = this.customHolidayMap;
        return new 日本の祝休日(builder.build());
    }
}
