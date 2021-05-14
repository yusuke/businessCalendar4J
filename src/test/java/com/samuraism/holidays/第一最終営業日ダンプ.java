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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.samuraism.holidays.Holidays.CLOSED_ON_SATURDAYS_AND_SUNDAYS;
import static com.samuraism.holidays.Japan.PUBLIC_HOLIDAYS;

/**
 * 第一営業日/最終営業日を向こう9年間計算してファイルにダンプ
 */
public class 第一最終営業日ダンプ {
    public static void main(String[] args) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        final LocalDate start = LocalDate.of(1955, 1, 1);
        final LocalDate now = LocalDate.now();
        final LocalDate end = LocalDate.of(now.getYear() + 9, 12, 31);
        final String shiftJisFileName = String.format("calculated/japan-first-last-business-days%s-%s-Shift_JIS.csv", start.format(formatter), end.format(formatter));
        final String utf8FileName = String.format("calculated/japan-first-last-business-days%s-%s-UTF8.csv", start.format(formatter), end.format(formatter));
        try (final BufferedWriter shiftJIS = Files.newBufferedWriter(Paths.get(shiftJisFileName), Charset.forName("Shift_JIS"));
             final BufferedWriter utf8 = Files.newBufferedWriter(Paths.get(utf8FileName), StandardCharsets.UTF_8)        ) {
            final String header = "年月日,営業日\n";
            shiftJIS.write(header);
            utf8.write(header);
            LocalDate cursor = start;
            Holidays holidays = Holidays.newBuilder().holiday(PUBLIC_HOLIDAYS).holiday(CLOSED_ON_SATURDAYS_AND_SUNDAYS).build();
            while (cursor.isBefore(end)) {
                LocalDate 月末 = cursor.withDayOfMonth(cursor.lengthOfMonth());
                LocalDate 最初営業日 = holidays.firstBusinessDay(cursor);
                LocalDate 最終営業日 = holidays.lastBusinessDay(月末);
                final String line = String.format("%s,%s\n%s,%s\n",最初営業日.format(dateTimeFormatter),"第一営業日",
                        最終営業日.format(dateTimeFormatter), "最終営業日");
                System.out.print(line);
                shiftJIS.write(line);
                utf8.write(line);
                cursor = cursor.plusMonths(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
