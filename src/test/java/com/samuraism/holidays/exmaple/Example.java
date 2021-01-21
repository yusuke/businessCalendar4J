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
package com.samuraism.holidays.exmaple;

import com.samuraism.holidays.日本の祝休日;
import com.samuraism.holidays.祝休日;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

public class Example {
    public static void main(String[] args) {
        日本の祝休日 holidays = new 日本の祝休日();

        // 元日なのでtrueが表示される
        System.out.println("2021年1月1日は祝日？: " + holidays.is祝休日(LocalDate.of(2021, 1, 1)));
        // 元日なのでfalseが表示される
        System.out.println("2021年1月1日は営業日？: " + holidays.is営業日(LocalDate.of(2021, 1, 1)));

        // 成人の日を取得
        Optional<祝休日> holiday = holidays.get祝休日(LocalDate.of(2021, 1, 11));
        holiday.ifPresent(e -> System.out.println("2021年1月11日は何の日？: " + e.名称));

        System.out.println("2021年5月の祝休日一覧: ");
        // 2021-05-03:憲法記念日、2021-05-04:みどりの日、2021-05-05:こどもの日 を表示
        holidays.get指定期間内の祝休日️(LocalDate.of(2021, 5, 1)
                , LocalDate.of(2021, 5, 31))
                .forEach(e -> System.out.println(e.日付 + ": " + e.名称));

        // 固定のカスタム祝休日を設定
        // メソッドチェーンで続けて書けるが、ミュータブルではなくオリジナルのインスタンスに変更が加わっていることに注意
        holidays.add祝休日(LocalDate.of(2013, 3, 29), "株式会社サムライズム設立")
                // ロジックベーのカスタム祝休日を設定。当該日が祝日ならば名称を、そうでなければnullを返す関数を指定する
                .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null)
                .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null)
                .add祝休日(e -> e.getMonthValue() == 12 && e.getDayOfMonth() == 31 ? "大晦日" : null);

        // 2021年1月最終営業日を取得→ 1月30日、31日が土日なので1月29日金曜日
        System.out.println("2021年最終営業日: " + holidays.最後の営業日(LocalDate.of(2021, 1, 31)));
        // 2020年大晦日以降最初の営業日を取得→ 1月1日は元日、1月2,3日はカスタム祝日(土日)なので1月4日月曜日
        System.out.println("2020年大晦日以降最初の営業日: " + holidays.最初の営業日(LocalDate.of(2020, 12, 31)));
        // 2021年2月22日以降最初の祝日を取得→ 2月23日 天皇誕生日
        System.out.println(holidays.最初の祝休日(LocalDate.of(2021, 2, 22)));
        // 2021年2月26日以前最初の祝日を取得→ 2月23日 天皇誕生日
        System.out.println(holidays.最後の祝休日(LocalDate.of(2021, 2, 26)));
    }
}
