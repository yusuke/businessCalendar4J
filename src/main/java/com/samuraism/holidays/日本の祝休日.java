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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class 日本の祝休日 {
    private final List<Function<LocalDate, String>> holidayLogics = new ArrayList<>();
    private static final 日本の祝休日アルゴリズム algorithm = new 日本の祝休日アルゴリズム();
    private final Map祝休日 custom祝休日Map = new Map祝休日();
    public 日本の祝休日() {
        holidayLogics.add(algorithm);
        holidayLogics.add(custom祝休日Map);
    }

    /**
     * ロジックベースの祝休日を追加。当該日が祝休日であれば名称を返す関数を指定する
     *
     * @param logic ロジック
     * @return このインスタンス
     */
    public 日本の祝休日 add祝休日(Function<LocalDate, String> logic) {
        holidayLogics.add(logic);
        return this;
    }

    /**
     * 固定の祝休日を追加
     *
     * @param 日付 日付
     * @param 名称 名称
     * @return このインスタンス
     */
    public 日本の祝休日 add祝休日(LocalDate 日付, String 名称) {
        custom祝休日Map.add祝休日(日付, 名称);
        return this;
    }

    /**
     * 指定した日が祝休日かどうかを判定する
     *
     * @param date 日付
     * @return 指定した日が祝休日であればtrue
     */
    public boolean is祝休日(LocalDate date) {
        return holidayLogics.stream().anyMatch(e -> e.apply(date) != null);
    }

    /**
     * 指定した日が営業日かどうかを判定する
     *
     * @param date 日付
     * @return 指定した日が営業日であればtrue
     */
    public boolean is営業日(LocalDate date) {
        return !is祝休日(date);
    }

    /**
     * 指定した日の祝休日を返す。
     *
     * @param date 日付
     * @return 祝日・休日
     */
    public Optional<祝休日> get祝休日(LocalDate date) {
        final Optional<String> first = holidayLogics.stream()
                .map(e -> e.apply(date)).filter(Objects::nonNull).findFirst();
        return first.map(s -> new 祝休日(date, s));
    }

    /**
     * 指定した日(指定した日を含む)以前で最初の営業日(祝休日ではない日)を返す
     *
     * @param date 指定日
     * @return 指定した日以前の営業日
     */
    public LocalDate 最後の営業日(LocalDate date) {
        LocalDate check = date;
        while (is祝休日(check)) {
            check = check.minus(1, ChronoUnit.DAYS);
        }
        return check;
    }

    /**
     * 指定した日以降(指定した日を含む)で最初の営業日(祝休日ではない日)を返す
     *
     * @param date 指定日
     * @return 指定した日以降の営業日
     */
    public LocalDate 最初の営業日(LocalDate date) {
        LocalDate check = date;
        while (is祝休日(check)) {
            check = check.plus(1, ChronoUnit.DAYS);
        }
        return check;
    }

    /**
     * 指定した日以前(指定した日を含む)の最後の祝休日を返す
     *
     * @param date 指定日
     * @return 指定した日以前の祝休日
     */
    public 祝休日 最後の祝休日(LocalDate date) {
        LocalDate check = date;
        while (!is祝休日(check)) {
            check = check.minus(1, ChronoUnit.DAYS);
        }
        //noinspection OptionalGetWithoutIsPresent
        return get祝休日(check).get();
    }

    /**
     * 指定した日(指定した日を含む)以降の最初の祝休日を返す
     *
     * @param date 指定日
     * @return 指定した日以前の祝休日
     */
    public 祝休日 最初の祝休日(LocalDate date) {
        LocalDate check = date;
        while (!is祝休日(check)) {
            check = check.plus(1, ChronoUnit.DAYS);
        }
        //noinspection OptionalGetWithoutIsPresent
        return get祝休日(check).get();
    }

    /**
     * 指定期間内の祝休日のリストを返す。リストは古い日から並べられている。指定期間内に祝休日がない場合は空のリストを返す。
     *
     * @param 開始日 指定開始日。この日も含む。
     * @param 終了日 指定終了日。この日も含む。
     * @return 指定期間内の祝休日のリスト。
     */
    public List<祝休日> get指定期間内の祝休日️(LocalDate 開始日, LocalDate 終了日) {
        List<祝休日> list = new ArrayList<>();
        LocalDate from = 開始日.isBefore(終了日) ? 開始日 : 終了日;
        LocalDate to = (終了日.isAfter(開始日) ? 終了日 : 開始日).plus(1, ChronoUnit.DAYS);
        while (from.isBefore(to)) {
            final Optional<祝休日> holiday = get祝休日(from);
            holiday.ifPresent(list::add);
            from = from.plus(1, ChronoUnit.DAYS);
        }
        return list;
    }
}
