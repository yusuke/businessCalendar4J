package com.samuraism.holidays;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.samuraism.holidays.UnitedStatesHolidays.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class UnitedStatesHolidaysTest {
    final UnitedStatesHolidays holidays = UnitedStatesHolidays.getInstance(conf -> conf.locale(Locale.ENGLISH)
            .holiday(
                    NEW_YEARS_DAY,
                    MARTIN_LUTHER_KING_JR_DAY,
                    MEMORIAL_DAY,
                    INDEPENDENCE_DAY,
                    LABOR_DAY,
                    VETERANS_DAY,
                    THANKS_GIVING_DAY,
                    CHRISTMAS_DAY
            ));

    @Test
    void newYearsDay() {
        assertEquals("New Year's Day", holidays.getHoliday(LocalDate.of(2021, 1, 1)).get().name);
    }

    @Test
    void martinLutherKingJrDay() {
        Map<Integer, Integer[]> myMap = new HashMap<Integer, Integer[]>() {
            {
                put(21, new Integer[]{1991, 2002, 2008, 2013, 2019, 2030, 2036, 2041, 2047, 2058, 2064, 2069, 2075, 2086, 2092, 2097});
                put(20, new Integer[]{1986, 1992, 1997, 2003, 2014, 2020, 2025, 2031, 2042, 2048, 2053, 2059, 2070, 2076, 2081, 2087, 2098});
                put(19, new Integer[]{1987, 1998, 2004, 2009, 2015, 2026, 2032, 2037, 2043, 2054, 2060, 2065, 2071, 2082, 2088, 2093, 2099});
                put(18, new Integer[]{1988, 1993, 1999, 2010, 2016, 2021, 2027, 2038, 2044, 2049, 2055, 2066, 2072, 2077, 2083, 2094, 2100});
                put(17, new Integer[]{1994, 2000, 2005, 2011, 2022, 2028, 2033, 2039, 2050, 2056, 2061, 2067, 2078, 2084, 2089, 2095});
                put(16, new Integer[]{1989, 1995, 2006, 2012, 2017, 2023, 2034, 2040, 2045, 2051, 2062, 2068, 2073, 2079, 2090, 2096});
                put(15, new Integer[]{1990, 1996, 2001, 2007, 2018, 2024, 2029, 2035, 2046, 2052, 2057, 2063, 2074, 2080, 2085, 2091});
            }
        };
        for (Integer day : myMap.keySet()) {
            final Integer[] years = myMap.get(day);
            for (Integer year : years) {
                assertEquals("Martin Luther King Jr. Day", holidays.getHoliday(LocalDate.of(year, 1, day)).get().name);
            }
        }
    }

    @Test
    void memorialDay() {
        Map<Integer, Integer[]> myMap = new HashMap<Integer, Integer[]>() {
            {
                put(31, new Integer[]{1971, 1976, 1982, 1993, 1999, 2004, 2010, 2021, 2027});
                put(30, new Integer[]{1977, 1983, 1988, 1994, 2005, 2011, 2016, 2022});
                put(29, new Integer[]{1972, 1978, 1989, 1995, 2000, 2006, 2017, 2023, 2028});
                put(28, new Integer[]{1973, 1979, 1984, 1990, 2001, 2007, 2012, 2018, 2029});
                put(27, new Integer[]{1974, 1985, 1991, 1996, 2002, 2013, 2019, 2024, 2030});
                put(26, new Integer[]{1975, 1980, 1986, 1997, 2003, 2008, 2014, 2025, 2031});
                put(25, new Integer[]{1981, 1987, 1992, 1998, 2009, 2015, 2020, 2026});
            }
        };
        for (Integer day : myMap.keySet()) {
            final Integer[] years = myMap.get(day);
            for (Integer year : years) {
                assertEquals("Memorial Day", holidays.getHoliday(LocalDate.of(year, 5, day)).get().name);
            }
        }
    }

    @Test
    void independenceDay() {
        // https://www.timeanddate.com/holidays/us/independence-day
        for (int year = 1776; year < 2100; year++) {
            final LocalDate date = LocalDate.of(year, 7, 4);
            final LocalDate previous = date.minus(1, ChronoUnit.DAYS);
            final LocalDate next = date.plus(1, ChronoUnit.DAYS);
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                assertTrue(holidays.isHoliday(previous));
                assertEquals("Independence Day (observed)", holidays.getHoliday(previous).get().name);
                assertTrue(holidays.isHoliday(date));
                assertEquals("Independence Day", holidays.getHoliday(date).get().name);
                assertFalse(holidays.isHoliday(next));
            } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                assertFalse(holidays.isHoliday(previous));
                assertTrue(holidays.isHoliday(date));
                assertEquals("Independence Day", holidays.getHoliday(date).get().name);
                assertTrue(holidays.isHoliday(next));
                assertEquals("Independence Day (observed)", holidays.getHoliday(next).get().name);
            } else {
                assertFalse(holidays.isHoliday(previous));
                assertTrue(holidays.isHoliday(date));
                assertEquals("Independence Day", holidays.getHoliday(date).get().name);
                assertFalse(holidays.isHoliday(next));
            }
        }
    }

    @Test
    void laborDay() {
        assertEquals("Labor Day", holidays.getHoliday(LocalDate.of(2021, 9, 6)).get().name);
    }

    @Test
    void veteransDay() {
        // https://www.timeanddate.com/holidays/us/veterans-day
        Map<Integer, Integer[]> myMap = new HashMap<Integer, Integer[]>() {
            {
                put(10, new Integer[]{2017, 2023});
                put(11, new Integer[]{2016, 2019, 2020, 2021, 2022, 2024, 2025, 2026});
                put(12, new Integer[]{2018});
            }
        };

        for (Integer day : myMap.keySet()) {
            final Integer[] years = myMap.get(day);
            for (Integer year : years) {
                final LocalDate previous = LocalDate.of(year, 11, 10);
                final LocalDate date = LocalDate.of(year, 11, 11);
                final LocalDate next = LocalDate.of(year, 11, 12);
                if (day.equals(10)) {
                    assertTrue(holidays.isHoliday(previous), previous.toString());
                    assertEquals("Veterans Day (observed)", holidays.getHoliday(previous).get().name);
                    assertTrue(holidays.isHoliday(date), date.toString());
                    assertEquals("Veterans Day", holidays.getHoliday(date).get().name);
                    assertFalse(holidays.isHoliday(next));
                } else if (day.equals(11)) {
                    assertFalse(holidays.isHoliday(previous), previous.toString());
                    assertTrue(holidays.isHoliday(date), date.toString());
                    assertEquals("Veterans Day", holidays.getHoliday(date).get().name);
                    assertFalse(holidays.isHoliday(next), next.toString());
                } else if (day.equals(12)) {
                    assertFalse(holidays.isHoliday(previous), previous.toString());
                    assertTrue(holidays.isHoliday(date), date.toString());
                    assertEquals("Veterans Day", holidays.getHoliday(date).get().name);
                    assertTrue(holidays.isHoliday(next), next.toString());
                    assertEquals("Veterans Day (observed)", holidays.getHoliday(next).get().name);
                }
            }
        }
    }

    @Test
    void thanksGivingDay() {
        Map<Integer, Integer[]> myMap = new HashMap<Integer, Integer[]>() {
            {
                put(22, new Integer[]{1990, 2001, 2007, 2012, 2018, 2029});
                put(23, new Integer[]{1989, 1995, 2000, 2006, 2017, 2023, 2028});
                put(24, new Integer[]{1988, 1994, 2005, 2011, 2016, 2022});
                put(25, new Integer[]{1993, 1999, 2004, 2010, 2021, 2027});
                put(26, new Integer[]{1987, 1992, 1998, 2009, 2015, 2020, 2026});
                put(27, new Integer[]{1986, 1997, 2003, 2008, 2014, 2025});
                put(28, new Integer[]{1985, 1991, 1996, 2002, 2013, 2019, 2024});
            }
        };
        for (Integer day : myMap.keySet()) {
            final Integer[] years = myMap.get(day);
            for (Integer year : years) {
                assertEquals("Thanksgiving Day", holidays.getHoliday(LocalDate.of(year, 11, day)).get().name);
            }
        }
    }

    @Test
    void christmasDay() {
        for (int year = 1; year < 2100; year++) {
            assertEquals("Christmas Day", holidays.getHoliday(LocalDate.of(year, 12, 24)).get().name);
        }
    }
}