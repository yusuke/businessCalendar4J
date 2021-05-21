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
    public ビジネスカレンダーBuilder 営業時間(String 営業時間) {
        builder.hours(営業時間);
        return this;
    }


    @NotNull
    public ビジネスカレンダーPredicate 曜日(@NotNull DayOfWeek... dayOfWeeks) {
        final BusinessCalendarBuilder.BusinessCalendarPredicate businessCalendarPredicate = builder.on(dayOfWeeks);
        return new ビジネスカレンダーPredicate(this, businessCalendarPredicate);
    }

    @NotNull
    public ビジネスカレンダー build() {
        return new ビジネスカレンダー(builder.build());
    }

    public static class ビジネスカレンダーPredicate {
        ビジネスカレンダーBuilder builder;
        BusinessCalendarBuilder.BusinessCalendarPredicate businessCalendarPredicate;

        public ビジネスカレンダーPredicate(ビジネスカレンダーBuilder builder, BusinessCalendarBuilder.BusinessCalendarPredicate businessCalendarPredicate) {
            this.builder = builder;
            this.businessCalendarPredicate = businessCalendarPredicate;
        }

        @NotNull
        public ビジネスカレンダーBuilder 営業時間(String 営業時間) {
            businessCalendarPredicate.hours(営業時間);
            return builder;
        }
    }
}
