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

import java.net.URL;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

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

    @NotNull
    public ビジネスカレンダーBuilder 営業時間(String 営業時間) {
        builder.hours(営業時間);
        return this;
    }

    @NotNull
    public ビジネスカレンダーPredicate 曜日(@NotNull DayOfWeek... dayOfWeeks) {
        final BusinessCalendarPredicate businessCalendarPredicate = builder.on(dayOfWeeks);
        return new ビジネスカレンダーPredicate(this, businessCalendarPredicate);
    }

    @NotNull
    public ビジネスカレンダーPredicate 曜日(int n番目, @NotNull DayOfWeek... dayOfWeeks) {
        final BusinessCalendarPredicate businessCalendarPredicate = builder.on(n番目, dayOfWeeks);
        return new ビジネスカレンダーPredicate(this, businessCalendarPredicate);
    }

    @NotNull
    public ビジネスカレンダーPredicate 年月日(int 年, int 月, int 日) {
        final BusinessCalendarPredicate businessCalendarPredicate = builder.on(年, 月, 日);
        return new ビジネスカレンダーPredicate(this, businessCalendarPredicate);
    }

    @NotNull
    public ビジネスカレンダーPredicate 月日(int 月, int 日) {
        final BusinessCalendarPredicate businessCalendarPredicate = builder.on(月, 日);
        return new ビジネスカレンダーPredicate(this, businessCalendarPredicate);
    }

    @NotNull
    public ビジネスカレンダーPredicate 条件(@NotNull Predicate<LocalDate> predicate) {
        final BusinessCalendarPredicate businessCalendarPredicate = builder.on(predicate);
        return new ビジネスカレンダーPredicate(this, businessCalendarPredicate);
    }

    /**
     * CSV設定ファイルを読み込む
     *
     * @param path csvファイルのパス
     * @return このインスタンス
     * @since 1.15
     */
    @NotNull
    public ビジネスカレンダーBuilder csv(@NotNull Path path) {
        builder.csv(path);
        return this;
    }

    /**
     * CSV設定ファイルを読み込む
     *
     * @param path     csvファイルのパス
     * @param duration リロード間隔
     * @return このインスタンス
     * @since 1.15
     */
    @NotNull
    public ビジネスカレンダーBuilder csv(@NotNull Path path, @NotNull Duration duration) {
        builder.csv(path, duration);
        return this;
    }

    /**
     * CSV設定をURLから読み込む
     *
     * @param url csvのURL
     * @return このインスタンス
     * @since 1.17
     */
    @NotNull
    public ビジネスカレンダーBuilder csv(@NotNull URL url) {
        builder.csv(url);
        return this;
    }

    /**
     * CSV設定をURLから読み込む
     *
     * @param url     csvファイルのパス
     * @param duration リロード間隔
     * @return このインスタンス
     * @since 1.17
     */
    @NotNull
    public ビジネスカレンダーBuilder csv(@NotNull URL url, @NotNull Duration duration) {
        builder.csv(url, duration);
        return this;
    }

    @NotNull
    public ビジネスカレンダー build() {
        return new ビジネスカレンダー(builder.build());
    }

    public static class ビジネスカレンダーPredicate {
        ビジネスカレンダーBuilder builder;
        BusinessCalendarPredicate businessCalendarPredicate;

        public ビジネスカレンダーPredicate(ビジネスカレンダーBuilder builder, BusinessCalendarPredicate businessCalendarPredicate) {
            this.builder = builder;
            this.businessCalendarPredicate = businessCalendarPredicate;
        }

        @NotNull
        public ビジネスカレンダーBuilder 営業時間(String 営業時間) {
            businessCalendarPredicate.hours(営業時間);
            return builder;
        }

        @NotNull
        public ビジネスカレンダーBuilder 祝休日(String 名称) {
            businessCalendarPredicate.holiday(名称);
            return builder;
        }

    }
}
