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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
@SuppressWarnings({"AccessStaticViaInstance", "ConstantConditions"})
public class 日本の祝休日アルゴリズムTest {
    static TreeMap<LocalDate, String> testCases;
    final static BusinessCalendar CALENDAR = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.JAPANESE).build();
    static Japan j = (Japan) Japan.PUBLIC_HOLIDAYS;

    static TreeMap<LocalDate, String> 祝休日Map待避;

    @BeforeAll
    static void init() throws IOException {
        // テスト用のデータセット
        // 内閣府のデータにはない2022年以降の未来の春分の日、秋分の日、スポーツの日を含む
        //noinspection ConstantConditions
        testCases = CSVHolidays.load(日本の祝休日アルゴリズムTest.class.getResourceAsStream("/syukujitsu-testcase.csv"), 
                ".japanese", Charset.forName("Shift_JIS"));
        祝休日Map待避 = j.csv.holidayMap;
        // 1970年1月1日元日(特にこの日付に意味は無い)まで残して、以降はアルゴリズムで答え合わせする
        j.csv.holidayMap = new TreeMap<>(祝休日Map待避.subMap(LocalDate.of(1955, 1, 1), LocalDate.of(1970, 1, 1)));
    }


    @AfterAll
    static void afterAll() {
        // 他のテストに影響を与えないよう、戻しておく
        j.csv.holidayMap = 祝休日Map待避;
    }

    @Test
    void 祝日() {
        assertAll(
                () -> {
                    // 1月1日	年のはじめを祝う。
                    check("元日");
                },
                () -> {
                    // 1月の第2月曜日	おとなになったことを自覚し、みずから生き抜こうとする青年を祝いはげます。
                    // 2000年より第二月曜日が成人の日
                    check("成人の日", LocalDate.of(2000, 1, 1));
                },
                () -> {
                    // 2月11日 政令で定める日	建国をしのび、国を愛する心を養う。
                    // https://ja.wikipedia.org/wiki/建国記念の日
                    // 紀元節復活の動きが高まり、「建国記念の日」として、1966年（昭和41年）に国民の祝日となり翌年から適用された。
                    check("建国記念の日", LocalDate.of(1967, 1, 1));
                },
                () -> {
                    // 2月23日	天皇の誕生日を祝う。
                    check("天皇誕生日", LocalDate.of(2020, 1, 1));
                },
                () -> {
                    // 春分日	自然をたたえ、生物をいつくしむ。
                    check("春分の日", LocalDate.of(2000, 1, 1));
                },
                () -> {
                    // 4月29日	激動の日々を経て、復興を遂げた昭和の時代を顧み、国の将来に思いをいたす。
                    check("昭和の日");
                },
                () -> {
                    // 5月3日	日本国憲法の施行を記念し、国の成長を期する。
                    check("憲法記念日");
                },
                () -> {
                    // 5月4日	自然に親しむとともにその恩恵に感謝し、豊かな心をはぐくむ。
                    // https://ja.wikipedia.org/wiki/みどりの日
                    // 1989年（平成元年）から2006年（平成18年）までは4月29日であった
                    check("みどりの日", LocalDate.of(2007, 1, 1));
                },
                () -> {
                    // 5月5日	こどもの人格を重んじ、こどもの幸福をはかるとともに、母に感謝する。
                    check("こどもの日");
                },
                () -> {
                    // 7月の第3月曜日	海の恩恵に感謝するとともに、海洋国日本の繁栄を願う。
                    // https://ja.wikipedia.org/wiki/海の日
                    // 制定当初は7月20日であったが、2003年（平成15年）に改正された祝日法のハッピーマンデー制度により、7月の第3月曜日となった。
                    check("海の日", LocalDate.of(2003, 1, 1), LocalDate.of(2019, 1, 1));
                },
                () -> {
                    // 8月11日	山に親しむ機会を得て、山の恩恵に感謝する。
                    // https://ja.wikipedia.org/wiki/山の日
                    // 2016年（平成28年）1月1日施行の改正祝日法で新設された。
                    // 2019年、2020年はオリンピックの関係で移動しているのでアルゴリズムでは算出できない
                    check("山の日", LocalDate.of(2016, 1, 1), LocalDate.of(2019, 1, 1));
                },
                () -> {
                    // 9月の第3月曜日	多年にわたり社会につくしてきた老人を敬愛し、長寿を祝う。
                    // https://ja.wikipedia.org/wiki/敬老の日
                    check("敬老の日", LocalDate.of(2003, 1, 1));
                },
                () -> {
                    // 秋分日	祖先をうやまい、なくなった人々をしのぶ。
                    check("秋分の日", LocalDate.of(2000, 1, 1));
                },
                () -> {
                    // 10月の第2月曜日	スポーツにしたしみ、健康な心身をつちかう。
                    check("スポーツの日", LocalDate.of(2022, 1, 1));
                },
                () -> {
                    // 11月3日	自由と平和を愛し、文化をすすめる。
                    check("文化の日");
                },
                () -> {
                    // 11月23日	勤労をたっとび、生産を祝い、国民たがいに感謝しあう。
                    check("勤労感謝の日");
                });
    }

    void check(String 祝日名) {
        check(祝日名, LocalDate.of(1955, 1, 1));
    }

    void check(String 祝日名, LocalDate from) {
        check(祝日名, from, LocalDate.of(2030, 12, 31));
    }

    void check(String 祝日名, LocalDate from, LocalDate to) {
        final List<LocalDate> list = testCases.entrySet().stream()
                .filter(localDateStringEntry -> localDateStringEntry.getValue().equals(祝日名))
                .map(Map.Entry::getKey).collect(Collectors.toList());
        for (LocalDate holiday : list) {
            if (holiday.isAfter(from)
                    && holiday.isBefore(to)) {
                try {
                    assertEquals(祝日名, CALENDAR.getHoliday(holiday).name, holiday.toString());
                } catch (NoSuchElementException e) {
                    fail(holiday.toString());
                }
            }

        }
    }


    @Test
    void 休日() {
        // https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html
        // 国民の祝日に関する法律第３条第２項に規定する休日（例）
        // いわゆる「振替休日」と呼ばれる休日です。
        // 「国民の祝日」が日曜日に当たるとき、その日の後の最も近い平日を休日とする
        //
        // 国民の祝日に関する法律第３条第３項に規定する休日（例）
        // 前日と翌日の両方を「国民の祝日」に挟まれた平日は休日となります。
        //「敬老の日」は「9月の第3月曜日」であるため9月15日から21日の間で移動します。
        //「秋分の日」は「秋分日」が9月22日か23日のいずれかで移動します。
        // このことにより数年に一度、不定期に現れる休日です。
        final List<LocalDate> list = testCases.entrySet().stream()
                .filter(localDateStringEntry -> localDateStringEntry.getValue().equals("休日"))
                .map(Map.Entry::getKey).collect(Collectors.toList());
        for (LocalDate holiday : list) {
            if (holiday.isAfter(LocalDate.of(2007, 1, 1))) {
                try {
                    assertEquals("休日", CALENDAR.getHoliday(holiday).name, holiday.toString());
                } catch (NoSuchElementException e) {
                    if (
                        // 以前の天皇誕生日の振替休日
                            !holiday.equals(LocalDate.of(2007, 12, 24)) &&
                                    !holiday.equals(LocalDate.of(2012, 12, 24)) &&
                                    !holiday.equals(LocalDate.of(2018, 12, 24)) &&
                                    // 即位礼正殿の儀前日
                                    !holiday.equals(LocalDate.of(2019, 4, 30)) &&
                                    // 即位礼正殿の儀翌日
                                    !holiday.equals(LocalDate.of(2019, 5, 2)) &&
                                    // オリンピックの関係で山の日が前日日曜日に移動しているため振替休日
                                    !holiday.equals(LocalDate.of(2021, 8, 9))

                    ) {
                        fail(holiday.toString());
                    }
                }
            }
        }
    }

    @Test
    void カスタム休日を指定しても休日算出が正しい() {
        BusinessCalendar businessCalendar = BusinessCalendar.newBuilder().holiday(Japan.PUBLIC_HOLIDAYS).locale(Locale.JAPANESE)
                .on(2022, 1, 2).holiday("休みたいから休む")
                .on(2007, 2, 12).holiday("休みたいから休む")
                .build();
        // 2022/1/1が元旦、かつ日曜日なので2022/1/2が休日
        // カスタム休日を2022/1/2に設定しても振替休日は2022/1/3にはならない
        assertFalse(businessCalendar.isHoliday(LocalDate.of(2022, 1, 3)));

        // 2007/2/11日が建国記念の日で日曜日なので2007/年2月12日は振替休日。カスタム祝休日の名称は出てこない
        assertEquals("休日", businessCalendar.getHoliday(LocalDate.of(2007, 2, 12)).name);
        assertFalse(businessCalendar.isHoliday(LocalDate.of(2007, 2, 13)));

    }
}

