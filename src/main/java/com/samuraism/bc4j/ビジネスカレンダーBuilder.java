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
    public 営業時間From 開始(int hour) {
        return 開始(hour, 0);
    }

    @NotNull
    public 営業時間From 開始(int hour, int minutes) {
        return new 営業時間From(builder.from(hour, minutes), this);
    }


    @NotNull
    public 営業時間 曜日(@NotNull DayOfWeek... dayOfWeeks) {
        return new 営業時間(this, dayOfWeeks);
    }

    @NotNull
    public ビジネスカレンダー build() {
        return new ビジネスカレンダー(builder.build());
    }

    public static class 営業時間 {
        DayOfWeek[] dayOfWeeks;
        private final ビジネスカレンダーBuilder builder;
        final BusinessCalendarBuilder.BusinessHour businessHour;

        public 営業時間(@NotNull ビジネスカレンダーBuilder builder, @NotNull DayOfWeek... dayOfWeeks) {
            this.dayOfWeeks = dayOfWeeks;
            this.builder = builder;
            businessHour = builder.builder.on(dayOfWeeks);
        }

        @NotNull
        public 営業時間From 開始(int 時) {
            return 開始(時, 0);
        }

        @NotNull
        public 営業時間From 開始(int 時, int 分) {
            return new 営業時間From(businessHour.from(時, 分), builder);
        }
    }


    public static class 営業時間From {
        private final BusinessCalendarBuilder.BusinessHourFrom businessHourFrom;
        private final ビジネスカレンダーBuilder builder;


        public 営業時間From(@NotNull BusinessCalendarBuilder.BusinessHourFrom businessHourFrom, @NotNull ビジネスカレンダーBuilder builder) {
            this.businessHourFrom = businessHourFrom;
            this.builder = builder;
        }

        @NotNull
        public ビジネスカレンダーBuilder 終了(int 時) {
            return 終了(時, 0);
        }

        @NotNull
        public ビジネスカレンダーBuilder 終了(int 時, int 分) {
            this.businessHourFrom.to(時, 分);
            return builder;
        }
    }
}
