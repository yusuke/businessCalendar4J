package com.samuraism.holidays;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class LocalizedHolidayName {
    @Test
    void localized() {
        assertEquals("Constitution Memorial Day",
                new JapaneseHolidays(Locale.ENGLISH).getHoliday(LocalDate.of(2040, 5, 3)).get().name);
        assertEquals("Constitution Memorial Day",
                new JapaneseHolidays(Locale.FRANCE).getHoliday(LocalDate.of(2040, 5, 3)).get().name);
        assertEquals("憲法記念日",
                new JapaneseHolidays(Locale.JAPANESE).getHoliday(LocalDate.of(2040, 5, 3)).get().name);

        
        assertEquals("Constitution Memorial Day", 
                new JapaneseHolidays(Locale.ENGLISH).getHoliday(LocalDate.of(2021, 5, 3)).get().name);
        assertEquals("Constitution Memorial Day", 
                new JapaneseHolidays(Locale.FRANCE).getHoliday(LocalDate.of(2021, 5, 3)).get().name);
        assertEquals("憲法記念日", 
                new JapaneseHolidays(Locale.JAPANESE).getHoliday(LocalDate.of(2021, 5, 3)).get().name);
    }
}
