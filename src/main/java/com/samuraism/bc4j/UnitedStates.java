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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.samuraism.bc4j.BusinessCalendarPredicate.holiday;
import static com.samuraism.bc4j.BusinessCalendarPredicate.predicate;

public class UnitedStates {


    public final Function<LocalDate, String> NEW_YEARS_DAY =
            substitution(predicate(1, 1), "unitedStates.NewYearsDay");
    public final Function<LocalDate, String> MARTIN_LUTHER_KING_JR_DAY =
            holiday(predicate(3, DayOfWeek.MONDAY, 1), "unitedStates.MartinLutherKingJrDay");
    public final Function<LocalDate, String> MEMORIAL_DAY =
            holiday(date -> date.getMonthValue() == 5 && date.getDayOfMonth() ==
                    date.with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY)).getDayOfMonth(), "unitedStates.MemorialDay");
    public final Function<LocalDate, String> INDEPENDENCE_DAY =
            substitution(predicate(7, 4), "unitedStates.IndependenceDay");
    public final Function<LocalDate, String> LABOR_DAY =
            holiday(predicate(1, DayOfWeek.MONDAY, 9), "unitedStates.LaborDay");
    public final Function<LocalDate, String> VETERANS_DAY =
            substitution(predicate(11, 11), "unitedStates.VeteransDay");
    public final Function<LocalDate, String> THANKS_GIVING_DAY =
            holiday(predicate(4, DayOfWeek.THURSDAY, 11), "unitedStates.ThanksgivingDay");
    public final Function<LocalDate, String> CHRISTMAS_DAY =
            substitution(predicate(12, 24), "unitedStates.ChristmasDay");


    private static Function<LocalDate, String> substitution(Predicate<LocalDate> predicate, String name) {
        return date -> {
            if (predicate.test(date)) {
                return name;
            }
            LocalDate movedFrom = null;
            if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                movedFrom = date.minus(1, ChronoUnit.DAYS);
            } else if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                movedFrom = date.plus(1, ChronoUnit.DAYS);
            }
            if (movedFrom != null) {
                if (predicate.test(movedFrom)) {
                    return "${" + name + "} (${unitedStates.observed})";
                }
            }
            return null;
        };
    }

    private UnitedStates() {
    }

    private static final UnitedStates singleton = new UnitedStates();

    static UnitedStates getInstance() {
        return singleton;
    }

    private final List<Function<LocalDate, String>> all = Arrays.asList(NEW_YEARS_DAY, MARTIN_LUTHER_KING_JR_DAY,
            MEMORIAL_DAY, INDEPENDENCE_DAY, LABOR_DAY, VETERANS_DAY, THANKS_GIVING_DAY, CHRISTMAS_DAY);

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

