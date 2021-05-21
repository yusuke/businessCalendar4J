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
package com.samuraism.bc4j.exmaple;

import com.samuraism.bc4j.ビジネスカレンダー;

import java.time.LocalDate;

public class ビジネスカレンダーExample {
    public static void main(String[] args) {
        ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder().祝休日(ビジネスカレンダー.日本の祝休日).build();

        // 元日なのでtrueが表示される
        System.out.println("2021年1月1日は祝日？: "
                + calendar.is祝休日(LocalDate.of(2021, 1, 1)));
        // 元日なのでfalseが表示される
        System.out.println("2021年1月1日は営業日？: "
                + calendar.is営業日(LocalDate.of(2021, 1, 1)));

        // 成人の日を取得
        System.out.println("2021年1月11日は何の日？: "
                + calendar.get祝休日(LocalDate.of(2021, 1, 11)));

        System.out.println("2021年5月の祝休日一覧: ");
        // 2021-05-03:憲法記念日、2021-05-04:みどりの日、2021-05-05:こどもの日 を表示
        calendar.get指定期間内の祝休日(LocalDate.of(2021, 5, 1)
                , LocalDate.of(2021, 5, 31))
                .forEach(e -> System.out.println(e.date + ": " + e.name));

        // 固定のカスタム祝休日を設定
        calendar = ビジネスカレンダー.newBuilder()
                .祝休日(ビジネスカレンダー.日本の祝休日)
                .日(1995, 5, 23).祝休日("Java誕生")
                .祝休日(ビジネスカレンダー.土日休業)
                // ロジックベーのカスタム祝休日を設定。当該日が祝日ならば名称を、そうでなければnullを返す関数を指定する
                .日(5, 19).祝休日("ジェームズ・ゴスリン誕生日")
                .build();

        // 2021年1月最終営業日を取得→ 1月30日、31日が土日なので1月29日金曜日
        System.out.println("2021年1月最終営業日: "
                + calendar.最後の営業日(LocalDate.of(2021, 1, 31)));
        // 2020年大晦日以降最初の営業日を取得→ 1月1日は元日、1月2,3日はカスタム祝日(土日)なので1月4日月曜日
        System.out.println("2020年大晦日以降最初の営業日: "
                + calendar.最初の営業日(LocalDate.of(2020, 12, 31)));
        // 2021年2月22日以降最初の祝日を取得→ 2月23日 天皇誕生日
        System.out.println(calendar.最初の祝休日(LocalDate.of(2021, 2, 22)));
        // 2021年2月26日以前最初の祝日を取得→ 2月23日 天皇誕生日
        System.out.println(calendar.最後の祝休日(LocalDate.of(2021, 2, 26)));
    }
}
