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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Function;

final class 日本の祝休日アルゴリズム implements Function<LocalDate, String> {
    @Override
    public String apply(LocalDate e) {
        final int year = e.getYear();
        final int month = e.getMonthValue();
        final int day = e.getDayOfMonth();
        if(month == 1 && day == 1){
            // 1955年1月1日以前を指定して以前の祝休日()を呼び出しても無限ループしないよう、元日だけは決め打ちで返す
            return "元日";
        }

        if (日本の祝休日.csv.祝休日Map.lastKey().isAfter(e)) {
            // 内閣府の公表しているデータの範囲内なのでアルゴリズムでは算出しない
            return null;
        }

        switch (month) {
            case 6:
            case 12:
                break;
            case 1:
                if (day == e.with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.MONDAY)).getDayOfMonth()) {
                    return "成人の日";
                }
                break;
            case 2:
                if (day == 11) {
                    return "建国記念の日";
                }
                if (day == 23) {
                    return "天皇誕生日";
                }
                break;
            case 3:
                final int[] 二十一日が春分の日の年 = {2002, 2003, 2006, 2007, 2010, 2011, 2014, 2015, 2018, 2019, 2022, 2023, 2027};

                switch (day) {
                    case 20:
                        for (int 二十一日year : 二十一日が春分の日の年) {
                            if (year == 二十一日year) {
                                return null;
                            }
                        }
                        return "春分の日";
                    case 21:
                        for (int 二十一日year : 二十一日が春分の日の年) {
                            if (year == 二十一日year) {
                                return "春分の日";
                            }
                        }
                }
                break;
            case 4:
                if (day == 29) {
                    return "昭和の日";
                }
                break;
            case 5:
                if (day == 3) {
                    return "憲法記念日";
                }
                if (day == 4) {
                    return "みどりの日";
                }
                if (day == 5) {
                    return "こどもの日";
                }
                break;
            case 7:
                if (day == e.with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY)).getDayOfMonth()) {
                    return "海の日";
                }
                break;
            case 8:
                if (day == 11) {
                    return "山の日";
                }
                break;
            case 9:
                final int[] 二十二日が秋分の日の年 = {2012, 2016, 2020, 2024, 2028};

                final int 敬老の日 = e.with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY)).getDayOfMonth();
                if (day == 敬老の日) {
                    return "敬老の日";
                }
                int 秋分の日 = 23;
                for (int 二十二日year : 二十二日が秋分の日の年) {
                    if (year == 二十二日year) {
                        秋分の日 = 22;
                        break;
                    }
                }
                // 国民の祝日に関する法律第３条第３項に規定する休日（例）
                // 前日と翌日の両方を「国民の祝日」に挟まれた平日は休日となります。
                //「敬老の日」は「9月の第3月曜日」であるため9月15日から21日の間で移動します。
                //「秋分の日」は「秋分日」が9月22日か23日のいずれかで移動します。
                // このことにより数年に一度、不定期に現れる休日です。
                if (((day - 1) == 敬老の日) && ((day + 1) == 秋分の日)) {
                    return "休日";
                }
                if (day == 秋分の日) {
                    return "秋分の日";
                }
                break;
            case 10:
                if (day == e.with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.MONDAY)).getDayOfMonth()) {
                    return "スポーツの日";
                }
                break;
            case 11:
                if (day == 3) {
                    return "文化の日";
                }
                if (day == 23) {
                    return "勤労感謝の日";
                }
                break;
        }
        // 国民の祝日に関する法律第３条第２項に規定する休日（例）
        // いわゆる「振替休日」と呼ばれる休日です。
        // 「国民の祝日」が日曜日に当たるとき、その日の後の最も近い平日を休日とする
        LocalDate test = e.minus(1, ChronoUnit.DAYS);
        while (test.getDayOfWeek() != DayOfWeek.SATURDAY) {
            // is祝休日で調べるとカスタム祝休日も含めて振替休日を算出してしまうので注意
            final String 導出休祝日 = this.apply(test);
            if (!日本の祝休日.csv.祝休日Map.containsKey(test) && (導出休祝日 == null || "休日".equals(導出休祝日))) {
                break;
            }
            if (test.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return "休日";
            }
            test = test.minus(1, ChronoUnit.DAYS);
        }
        return null;
    }
}

