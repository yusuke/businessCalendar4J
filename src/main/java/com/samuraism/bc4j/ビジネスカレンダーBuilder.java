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
package com.samuraism.bc4j;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Function;

public final class ビジネスカレンダーBuilder {
    final BusinessCalendarBuilder builder = BusinessCalendar.newBuilder();

    ビジネスカレンダーBuilder() {
    }

    @NotNull
    public final ビジネスカレンダーBuilder locale(@NotNull Locale locale) {
        builder.locale(locale);
        return this;
    }

    /**
     * ロジックベースの祝休日を追加する.
     *
     * @param logics logics
     * @return このインスタンス
     */
    @SafeVarargs
    @NotNull
    public final ビジネスカレンダーBuilder 祝休日(@NotNull Function<LocalDate, String>... logics) {
        builder.holiday(logics);
        return this;
    }

    /**
     * 固定の祝休日を追加する
     *
     * @param 日付 日付
     * @param 名称 名称
     * @return このインスタンス
     */
    @NotNull
    public ビジネスカレンダーBuilder 祝休日(@NotNull LocalDate 日付, @NotNull String 名称) {
        builder.holiday(日付, 名称);
        return this;
    }

    @NotNull
    public 営業時間From 営業時間(int hour) {
        return 営業時間(hour, 0);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, int hour) {
        return 営業時間(dayOfWeek1, hour, 0);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2, int hour) {
        return 営業時間(dayOfWeek1, dayOfWeek2, hour, 0);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2, @NotNull DayOfWeek dayOfWeek3, int hour) {
        return 営業時間(dayOfWeek1, dayOfWeek2, dayOfWeek3, hour, 0);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                         @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4, int hour) {
        return 営業時間(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, hour, 0);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                         @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4, @NotNull DayOfWeek dayOfWeek5,
                         int hour) {
        return 営業時間(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, hour, 0);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                         @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4, @NotNull DayOfWeek dayOfWeek5,
                         @NotNull DayOfWeek dayOfWeek6, int hour) {
        return 営業時間(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, dayOfWeek6, hour, 0);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                         @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4, @NotNull DayOfWeek dayOfWeek5,
                         @NotNull DayOfWeek dayOfWeek6, @NotNull DayOfWeek dayOfWeek7, int hour) {
        return 営業時間(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, dayOfWeek6, dayOfWeek7, hour, 0);
    }

    @NotNull
    public 営業時間From 営業時間(int hour, int minutes) {
        return new 営業時間From(builder.businessHourFrom(hour, minutes), this);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, int hour, int minutes) {
        return new 営業時間From(builder.businessHourFrom(dayOfWeek1, hour, minutes), this);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2, int hour, int minutes) {
        return new 営業時間From(builder.businessHourFrom(dayOfWeek1, dayOfWeek2, hour, minutes), this);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                         @NotNull DayOfWeek dayOfWeek3, int hour, int minutes) {
        return new 営業時間From(builder.businessHourFrom(dayOfWeek1, dayOfWeek2, dayOfWeek3, hour, minutes), this);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                         @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4, int hour, int minutes) {
        return new 営業時間From(builder.businessHourFrom(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, hour, minutes), this);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                         @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4, @NotNull DayOfWeek dayOfWeek5,
                         int hour, int minutes) {
        return new 営業時間From(builder.businessHourFrom(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5,
                hour, minutes), this);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                         @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4, @NotNull DayOfWeek dayOfWeek5,
                         @NotNull DayOfWeek dayOfWeek6, int hour, int minutes) {
        return new 営業時間From(builder.businessHourFrom(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, dayOfWeek6, hour, minutes), this);
    }

    @NotNull
    public 営業時間From 営業時間(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                         @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4, @NotNull DayOfWeek dayOfWeek5,
                         @NotNull DayOfWeek dayOfWeek6, @NotNull DayOfWeek dayOfWeek7, int hour, int minutes) {
        return new 営業時間From(builder.businessHourFrom(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, dayOfWeek6, dayOfWeek7, hour, minutes), this);
    }

    @NotNull
    public ビジネスカレンダー build() {
        return new ビジネスカレンダー(builder.build());
    }

    public static class 営業時間From {
        private final BusinessCalendarBuilder.BusinessHourFrom businessHourFrom;
        private final ビジネスカレンダーBuilder builder;


        public 営業時間From(@NotNull BusinessCalendarBuilder.BusinessHourFrom businessHourFrom, @NotNull ビジネスカレンダーBuilder builder) {
            this.businessHourFrom = businessHourFrom;
            this.builder = builder;
        }

        @NotNull
        public ビジネスカレンダーBuilder から(int 時) {
            return から(時, 0);
        }

        @NotNull
        public ビジネスカレンダーBuilder から(int 時, int 分) {
            this.businessHourFrom.to(時, 分);
            return builder;
        }
    }
}
