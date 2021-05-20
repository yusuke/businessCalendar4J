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

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class 営業時間Example {
    public static void main(String[] args) {
        ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder()
                // 月曜〜金曜は9時〜18時営業
                .開始(9).終了(18)
                // 土日は10時〜16:30営業
                .曜日(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).開始(10).終了(16, 30)
                .build();
        // true をプリント
        System.out.println("2021年5月20(木) 9:30 は営業時間？ :" +
                calendar.is営業時間(LocalDateTime.of(2021, 5, 20, 9, 30)));
        // false をプリント
        System.out.println("2021年5月22(土) 9:30 は営業時間？ :" +
                calendar.is営業時間(LocalDateTime.of(2021, 5, 22, 9, 30)));
    }
}
