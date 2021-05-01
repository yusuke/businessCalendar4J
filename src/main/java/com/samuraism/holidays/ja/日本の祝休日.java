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
package com.samuraism.holidays.ja;

import com.samuraism.holidays.Holiday;
import com.samuraism.holidays.JapaneseHolidays;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class 日本の祝休日 {
    private final JapaneseHolidays holidays;

    /**
     * 正月三が日を休業とするアルゴリズム
     *
     * @since 1.5
     */
    public static final Function<LocalDate, String> 正月三が日休業 = JapaneseHolidays.CLOSED_ON_NEW_YEARS_HOLIDAYS;


    /**
     * 大晦日を休業とするアルゴリズム
     *
     * @since 1.5
     */
    public static final Function<LocalDate, String> 大晦日休業 = JapaneseHolidays.CLOSED_ON_NEW_YEARS_EVE;

    /**
     * 大晦日を休業とするアルゴリズム
     * @since 1.5
     */
    public static final Function<LocalDate, String> 土日休業 = JapaneseHolidays.CLOSED_ON_SATURDAYS_AND_SUNDAYS;


    public 日本の祝休日() {
        holidays = new JapaneseHolidays();
    }

    public 日本の祝休日(Locale locale) {
        holidays = new JapaneseHolidays(locale);
    }

    /**
     * ロジックベースの祝休日を追加。当該日が祝休日であれば名称を返す関数を指定する
     *
     * @param logic ロジック
     * @return このインスタンス
     */
    public 日本の祝休日 add祝休日(Function<LocalDate, String> logic) {
        holidays.addHoliday(logic);
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
        holidays.addHoliday(日付, 名称);
        return this;
    }


    /**
     * 指定した日が祝休日かどうかを判定する
     *
     * @param date 日付
     * @return 指定した日が祝休日であればtrue
     */
    public boolean is祝休日(LocalDate date) {
        return holidays.isHoliday(date);
    }

    /**
     * 今日が祝休日かどうかを判定する
     *
     * @return 今日が祝休日であればtrue
     * @since 1.3
     */
    public boolean is祝休日() {
        return holidays.isHoliday();
    }

    /**
     * 指定した日が営業日かどうかを判定する
     *
     * @param date 日付
     * @return 指定した日が営業日であればtrue
     */
    public boolean is営業日(LocalDate date) {
        return holidays.isBusinessDay(date);
    }

    /**
     * 今日が営業日かどうかを判定する
     *
     * @return 今日が営業日であればtrue
     * @since 1.3
     */
    public boolean is営業日() {
        return holidays.isBusinessDay();
    }

    /**
     * 指定した日の祝休日を返す。
     *
     * @param date 日付
     * @return 祝日・休日
     */
    public Optional<祝休日> get祝休日(LocalDate date) {
        final Optional<Holiday> holiday = holidays.getHoliday(date);
        return holiday.map(祝休日::new);
    }

    /**
     * 今日以前(今日を含む)で最後の営業日(祝休日ではない日)を返す
     *
     * @param date 指定日
     * @return 今日以前の営業日
     * @since 1.4
     */
    public LocalDate 最後の営業日(LocalDate date) {
        return holidays.lastBusinessDay(date);
    }

    /**
     * 今日以前(今日を含む)で最後の営業日(祝休日ではない日)を返す
     *
     * @return 今日以前の営業日
     * @since 1.4
     */
    public LocalDate 最後の営業日() {
        return holidays.lastBusinessDay();
    }

    /**
     * 指定した日以降(指定した日を含む)で最初の営業日(祝休日ではない日)を返す
     *
     * @param date 指定日
     * @return 指定した日以降の営業日
     */
    public LocalDate 最初の営業日(LocalDate date) {
        return holidays.firstBusinessDay(date);
    }

    /**
     * 今日以降(今日を含む)で最初の営業日(祝休日ではない日)を返す
     *
     * @return 今日以降の営業日
     * @since 1.4
     */
    public LocalDate 最初の営業日() {
        return holidays.firstBusinessDay();
    }

    /**
     * 指定した日以前(指定した日を含む)の最後の祝休日を返す
     *
     * @param date 指定日
     * @return 指定した日以前の祝休日
     */
    public 祝休日 最後の祝休日(LocalDate date) {
        return new 祝休日(holidays.lastHoliday(date));
    }

    /**
     * 今日以前(今日を含む)の最後の祝休日を返す
     *
     * @return 今日以前の祝休日
     * @since 1.4
     */
    public 祝休日 最後の祝休日() {
        return new 祝休日(holidays.lastHoliday());
    }

    /**
     * 指定した日(指定した日を含む)以降の最初の祝休日を返す
     *
     * @param date 指定日
     * @return 指定した日以前の祝休日
     */
    public 祝休日 最初の祝休日(LocalDate date) {
        return new 祝休日(holidays.firstHoliday(date));
    }

    /**
     * 今日(今日を含む)以降の最初の祝休日を返す
     *
     * @return 今日以降の祝休日
     * @since 1.4
     */
    public 祝休日 最初の祝休日() {
        return new 祝休日(holidays.firstHoliday());
    }

    /**
     * 指定期間内の祝休日のリストを返す。リストは古い日から並べられている。指定期間内に祝休日がない場合は空のリストを返す。
     *
     * @param 開始日 指定開始日。この日も含む。
     * @param 終了日 指定終了日。この日も含む。
     * @return 指定期間内の祝休日のリスト。
     */
    public List<祝休日> get指定期間内の祝休日(LocalDate 開始日, LocalDate 終了日) {
        return holidays.getHolidaysBetween️(開始日, 終了日).stream().map(祝休日::new).collect(Collectors.toList());
    }

    /**
     * <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">内閣府で公表されている祝休日情報</a>の初日を返します。この日より前の祝休日は現行の法律、国立天文台の情報を元にアルゴリズムで求められた祝休日になります。
     *
     * @return 内閣府で公表されている祝休日情報の初日
     * @since 1.4
     */
    public LocalDate get内閣府公表祝休日初日(){
        return  holidays.getCabinetOfficialHolidayDataFirstDay();
    }

    /**
     * <a href="https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html">内閣府で公表されている祝休日情報</a>の最終日を返します。この日より後の祝休日は現行の法律、国立天文台の情報を元にアルゴリズムで求められた祝休日になります。
     *
     * @return 内閣府で公表されている祝休日情報の最終日
     * @since 1.4
     */
    public LocalDate get内閣府公表祝休日最終日(){
        return holidays.getCabinetOfficialHolidayDataLastDay();
    }
}
