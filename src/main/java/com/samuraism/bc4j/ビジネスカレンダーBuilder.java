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

import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Function;

public final class ビジネスカレンダーBuilder {
    final BusinessCalendarBuilder builder = BusinessCalendar.newBuilder();

    ビジネスカレンダーBuilder() {
    }

    public final ビジネスカレンダーBuilder locale(Locale locale) {
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
    public final ビジネスカレンダーBuilder 祝休日(Function<LocalDate, String>... logics) {
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
    public ビジネスカレンダーBuilder 祝休日(LocalDate 日付, String 名称) {
        builder.holiday(日付,名称);
        return this;
    }

    public ビジネスカレンダー build() {
        return new ビジネスカレンダー(builder.build());
    }
}
