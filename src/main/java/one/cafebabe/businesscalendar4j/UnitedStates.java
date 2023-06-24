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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static one.cafebabe.businesscalendar4j.BusinessCalendarPredicate.holiday;
import static one.cafebabe.businesscalendar4j.BusinessCalendarPredicate.predicate;

/**
 * Holidays in the United States
 */
public class UnitedStates {


    /**
     * New Year's Day
     */
    public final Function<LocalDate, String> NEW_YEARS_DAY =
            substitution(predicate(1, 1), "unitedStates.NewYearsDay");
    /**
     * Martin Luther King Jr. Day
     */
    public final Function<LocalDate, String> MARTIN_LUTHER_KING_JR_DAY =
            holiday(predicate(3, DayOfWeek.MONDAY, 1), "unitedStates.MartinLutherKingJrDay");
    /**
     * <a href="https://en.wikipedia.org/wiki/Memorial_Day">Memorial Day</a>
     */
    public final Function<LocalDate, String> MEMORIAL_DAY =
            holiday(date -> date.getMonthValue() == 5 && date.getDayOfMonth() ==
                    date.with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY)).getDayOfMonth(), "unitedStates.MemorialDay");
    /**
     * <a href="https://www.imdb.com/title/tt0116629/">Independence Day/</a>
     */
    public final Function<LocalDate, String> JUNETEENTH_DAY =
            substitution(2023, predicate(6, 19), "unitedStates.JuneteenthDay");
    public final Function<LocalDate, String> INDEPENDENCE_DAY =
            substitution(predicate(7, 4), "unitedStates.IndependenceDay");
    /**
     * Labor Day
     * <a href="https://en.wikipedia.org/wiki/Labor_Day">https://en.wikipedia.org/wiki/Labor_Day</a>
     */
    public final Function<LocalDate, String> LABOR_DAY =
            holiday(predicate(1, DayOfWeek.MONDAY, 9), "unitedStates.LaborDay");
    /**
     * <a href="https://en.wikipedia.org/wiki/Veterans_Day">Veterans' Day</a>
     */
    public final Function<LocalDate, String> VETERANS_DAY =
            substitution(predicate(11, 11), "unitedStates.VeteransDay");
    /**
     * <a href="https://en.wikipedia.org/wiki/Thanksgiving">Thanksgiving</a>
     */
    public final Function<LocalDate, String> THANKSGIVING_DAY =
            holiday(predicate(4, DayOfWeek.THURSDAY, 11), "unitedStates.ThanksgivingDay");
    /**
     * <a href="https://en.wikipedia.org/wiki/Christmas">Christmas Day</a>
     */
    public final Function<LocalDate, String> CHRISTMAS_DAY =
            substitution(predicate(12, 25), "unitedStates.ChristmasDay");


    private static Function<LocalDate, String> substitution(Predicate<LocalDate> predicate, String name) {
        return date -> {
            if (predicate.test(date)) {
                return name;
            }
            LocalDate movedFrom = null;
            if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                movedFrom = date.minusDays(1);
            } else if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                movedFrom = date.plusDays(1);
            }
            if (movedFrom != null) {
                if (predicate.test(movedFrom)) {
                    return "${" + name + "} (${unitedStates.observed})";
                }
            }
            return null;
        };
    }
    private static Function<LocalDate, String> substitution(int startFromYear, Predicate<LocalDate> predicate, String name) {
        Function<LocalDate, String> substitution = substitution(predicate, name);
        return date -> {
            if (date.getYear() < startFromYear) {
                return null;
            }
            return substitution.apply(date);
        };
    }

    private UnitedStates() {
    }

    private static final UnitedStates singleton = new UnitedStates();

    static UnitedStates getInstance() {
        return singleton;
    }

    private final List<Function<LocalDate, String>> all = Arrays.asList(NEW_YEARS_DAY, MARTIN_LUTHER_KING_JR_DAY,
            MEMORIAL_DAY, JUNETEENTH_DAY, INDEPENDENCE_DAY, LABOR_DAY, VETERANS_DAY, THANKSGIVING_DAY, CHRISTMAS_DAY);

    /**
     * Public holidays in the United States
     */
    public Function<LocalDate, String> PUBLIC_HOLIDAYS = localDate -> {
        for (Function<LocalDate, String> localDateStringFunction : all) {
            final String apply = localDateStringFunction.apply(localDate);
            if (apply != null) {
                return apply;
            }
        }
        return null;
    };
}

