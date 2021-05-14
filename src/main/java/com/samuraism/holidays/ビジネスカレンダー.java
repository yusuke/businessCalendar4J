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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ビジネスカレンダー {
    private final BusinessCalendar businessCalendar;

    /**
     * 正月三が日を休業とするアルゴリズム
     *
     * @since 1.5
     */
    public static final Function<LocalDate, String> 正月三が日休業 = Japan.CLOSED_ON_NEW_YEARS_HOLIDAYS;

    /**
     * 日本の祝休日を算出するアルゴリズム
     */
    public static final Function<LocalDate, String> 日本の祝休日 = Japan.PUBLIC_HOLIDAYS;

    /**
     * アメリカ合衆国の祝休日を算出するアルゴリズム
     */
    public static final Function<LocalDate, String>[] アメリカ合衆国の祝休日 = UnitedStates.PUBLIC_HOLIDAYS;

    /**
     * 大晦日を休業とするアルゴリズム
     *
     * @since 1.5
     */
    public static final Function<LocalDate, String> 大晦日休業 = Japan.CLOSED_ON_NEW_YEARS_EVE;

    /**
     * 大晦日を休業とするアルゴリズム
     * @since 1.5
     */
    public static final Function<LocalDate, String> 土日休業 = BusinessCalendar.CLOSED_ON_SATURDAYS_AND_SUNDAYS;

    ビジネスカレンダー(BusinessCalendar businessCalendar) {
        this.businessCalendar = businessCalendar;
    }

    public static ビジネスカレンダーBuilder newBuilder(){
        return new ビジネスカレンダーBuilder();
    }

    /**
     * 指定した日が祝休日かどうかを判定する
     *
     * @param date 日付
     * @return 指定した日が祝休日であればtrue
     */
    public boolean is祝休日(LocalDate date) {
        return businessCalendar.isHoliday(date);
    }

    /**
     * 今日が祝休日かどうかを判定する
     *
     * @return 今日が祝休日であればtrue
     * @since 1.3
     */
    public boolean is祝休日() {
        return businessCalendar.isHoliday();
    }

    /**
     * 指定した日が営業日かどうかを判定する
     *
     * @param date 日付
     * @return 指定した日が営業日であればtrue
     */
    public boolean is営業日(LocalDate date) {
        return businessCalendar.isBusinessDay(date);
    }

    /**
     * 今日が営業日かどうかを判定する
     *
     * @return 今日が営業日であればtrue
     * @since 1.3
     */
    public boolean is営業日() {
        return businessCalendar.isBusinessDay();
    }

    /**
     * 指定した日の祝休日を返す。
     *
     * @param date 日付
     * @return 祝日・休日
     */
    public Optional<Holiday> get祝休日(LocalDate date) {
        return businessCalendar.getHoliday(date);
    }

    /**
     * 今日以前(今日を含む)で最後の営業日(祝休日ではない日)を返す
     *
     * @param date 指定日
     * @return 今日以前の営業日
     * @since 1.4
     */
    public LocalDate 最後の営業日(LocalDate date) {
        return businessCalendar.lastBusinessDay(date);
    }

    /**
     * 今日以前(今日を含む)で最後の営業日(祝休日ではない日)を返す
     *
     * @return 今日以前の営業日
     * @since 1.4
     */
    public LocalDate 最後の営業日() {
        return businessCalendar.lastBusinessDay();
    }

    /**
     * 指定した日以降(指定した日を含む)で最初の営業日(祝休日ではない日)を返す
     *
     * @param date 指定日
     * @return 指定した日以降の営業日
     */
    public LocalDate 最初の営業日(LocalDate date) {
        return businessCalendar.firstBusinessDay(date);
    }

    /**
     * 今日以降(今日を含む)で最初の営業日(祝休日ではない日)を返す
     *
     * @return 今日以降の営業日
     * @since 1.4
     */
    public LocalDate 最初の営業日() {
        return businessCalendar.firstBusinessDay();
    }

    /**
     * 指定した日以前(指定した日を含む)の最後の祝休日を返す
     *
     * @param date 指定日
     * @return 指定した日以前の祝休日
     */
    public Holiday 最後の祝休日(LocalDate date) {
        return businessCalendar.lastHoliday(date);
    }

    /**
     * 今日以前(今日を含む)の最後の祝休日を返す
     *
     * @return 今日以前の祝休日
     * @since 1.4
     */
    public Holiday 最後の祝休日() {
        return businessCalendar.lastHoliday();
    }

    /**
     * 指定した日(指定した日を含む)以降の最初の祝休日を返す
     *
     * @param date 指定日
     * @return 指定した日以前の祝休日
     */
    public Holiday 最初の祝休日(LocalDate date) {
        return businessCalendar.firstHoliday(date);
    }

    /**
     * 今日(今日を含む)以降の最初の祝休日を返す
     *
     * @return 今日以降の祝休日
     * @since 1.4
     */
    public Holiday 最初の祝休日() {
        return businessCalendar.firstHoliday();
    }

    /**
     * 指定期間内の祝休日のリストを返す。リストは古い日から並べられている。指定期間内に祝休日がない場合は空のリストを返す。
     *
     * @param 開始日 指定開始日。この日も含む。
     * @param 終了日 指定終了日。この日も含む。
     * @return 指定期間内の祝休日のリスト。
     */
    public List<Holiday> get指定期間内の祝休日(LocalDate 開始日, LocalDate 終了日) {
        return businessCalendar.getHolidaysBetween️(開始日, 終了日);
    }

    /**
     * <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">内閣府で公表されている祝休日情報</a>の初日を返します。この日より前の祝休日は現行の法律、国立天文台の情報を元にアルゴリズムで求められた祝休日になります。
     *
     * @return 内閣府で公表されている祝休日情報の初日
     * @since 1.4
     */
    public LocalDate get内閣府公表祝休日初日(){
        return Japan.getCabinetOfficialHolidayDataFirstDay();
    }

    /**
     * <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">内閣府で公表されている祝休日情報</a>の最終日を返します。この日より後の祝休日は現行の法律、国立天文台の情報を元にアルゴリズムで求められた祝休日になります。
     *
     * @return 内閣府で公表されている祝休日情報の最終日
     * @since 1.4
     */
    public LocalDate get内閣府公表祝休日最終日(){
        return Japan.getCabinetOfficialHolidayDataLastDay();
    }
}
