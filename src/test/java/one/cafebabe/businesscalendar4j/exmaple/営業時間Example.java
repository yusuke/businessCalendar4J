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
package one.cafebabe.businesscalendar4j.exmaple;

import one.cafebabe.businesscalendar4j.ビジネスカレンダー;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class 営業時間Example {
    public static void main(String[] args) {
        ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder()
                // 大晦日は10時半〜12時、午後1時〜午後3時
                .月日(12, 31).営業時間("午前10時半〜正午,13時から15pm")
                // 土日は10時〜12時、13時〜16:30
                .曜日(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).営業時間("10:00 A.M. - 11:30 am, 正午から午後4時半")
                // 月曜〜金曜は9時〜午後6時
                .営業時間("9-18")
                .build();

        // true をプリント
        System.out.println("2021年5月20(木) 9:30 は営業時間？ :" +
                calendar.is営業時間(LocalDateTime.of(2021, 5, 20, 9, 30)));
        // false をプリント
        System.out.println("2021年5月22(土) 9:30 は営業時間？ :" +
                calendar.is営業時間(LocalDateTime.of(2021, 5, 22, 9, 30)));
    }
}
