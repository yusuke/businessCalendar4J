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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.samuraism.bc4j.BusinessCalendar.UNITED_STATES;

/**
 * Dump United States holidays
 */
public class DumpUnitedStatesHolidays {
    public static void main(String[] args) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        final LocalDate start = LocalDate.of(1970, 1, 1);
        final LocalDate now = LocalDate.now();
        final LocalDate end = LocalDate.of(now.getYear() + 9, 12, 31);
        final String utf8FileName = String.format("calculated/unitedStates-holidays%s-%s-UTF8.csv", start.format(formatter), end.format(formatter));
        try (final BufferedWriter utf8 = Files.newBufferedWriter(Paths.get(utf8FileName), StandardCharsets.UTF_8)) {
            final String header = "date,name\n";
            utf8.write(header);
            for (Holiday holiday : BusinessCalendar.newBuilder()
                    .locale(Locale.ENGLISH)
                    .holiday(UNITED_STATES.NEW_YEARS_DAY,
                            UNITED_STATES.MARTIN_LUTHER_KING_JR_DAY,
                            UNITED_STATES.MEMORIAL_DAY,
                            UNITED_STATES.INDEPENDENCE_DAY,
                            UNITED_STATES.LABOR_DAY,
                            UNITED_STATES.VETERANS_DAY,
                            UNITED_STATES.THANKS_GIVING_DAY,
                            UNITED_STATES.CHRISTMAS_DAY)
                    .build()
                    .getHolidaysBetween(start, end)) {
                final String line = String.format("%s,%s\n", holiday.date.format(dateTimeFormatter), holiday.name);
                System.out.print(line);
                utf8.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
