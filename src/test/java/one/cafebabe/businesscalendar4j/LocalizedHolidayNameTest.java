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
package one.cafebabe.businesscalendar4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.CONCURRENT)
@SuppressWarnings({"ConstantConditions"})
public class LocalizedHolidayNameTest {
    @Test
    void localized() {
        final boolean isLocaleJapanese = Locale.getDefault().getLanguage().equals("ja");
        assertEquals("Constitution Memorial Day",
                BusinessCalendar.newBuilder().holiday(BusinessCalendar.JAPAN.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH).build().getHoliday(LocalDate.of(2040, 5, 3)).name());
        assertEquals(isLocaleJapanese ? "憲法記念日" : "Constitution Memorial Day",
                BusinessCalendar.newBuilder().holiday(BusinessCalendar.JAPAN.PUBLIC_HOLIDAYS).locale(Locale.FRANCE).build().getHoliday(LocalDate.of(2040, 5, 3)).name());
        assertEquals("憲法記念日",
                BusinessCalendar.newBuilder().holiday(BusinessCalendar.JAPAN.PUBLIC_HOLIDAYS).locale(Locale.JAPANESE).build().getHoliday(LocalDate.of(2040, 5, 3)).name());


        assertEquals("Constitution Memorial Day",
                BusinessCalendar.newBuilder().holiday(BusinessCalendar.JAPAN.PUBLIC_HOLIDAYS).locale(Locale.ENGLISH).build().getHoliday(LocalDate.of(2021, 5, 3)).name());
        assertEquals(isLocaleJapanese ? "憲法記念日" : "Constitution Memorial Day",
                BusinessCalendar.newBuilder().holiday(BusinessCalendar.JAPAN.PUBLIC_HOLIDAYS).locale(Locale.FRANCE).build().getHoliday(LocalDate.of(2021, 5, 3)).name());
        assertEquals("憲法記念日",
                BusinessCalendar.newBuilder().holiday(BusinessCalendar.JAPAN.PUBLIC_HOLIDAYS).locale(Locale.JAPANESE).build().getHoliday(LocalDate.of(2021, 5, 3)).name());
    }
}
