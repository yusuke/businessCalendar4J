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

public class UnitedStates {


    public static final Function<LocalDate, String> NEW_YEARS_DAY = e -> substitution(e, e2 -> e2.getMonthValue() == 1 && e2.getDayOfMonth() == 1 ? "unitedStates.NewYearsDay" : null);
    public static final Function<LocalDate, String> MARTIN_LUTHER_KING_JR_DAY = e -> e.getMonthValue() == 1 && e.getDayOfMonth() ==
            e.with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY)).getDayOfMonth() ? "unitedStates.MartinLutherKingJrDay" : null;
    public static final Function<LocalDate, String> MEMORIAL_DAY = e -> e.getMonthValue() == 5 && e.getDayOfMonth() ==
            e.with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY)).getDayOfMonth() ? "unitedStates.MemorialDay" : null;
    public static final Function<LocalDate, String> INDEPENDENCE_DAY = e -> substitution(e, e2 -> e2.getMonthValue() == 7 && e2.getDayOfMonth() == 4 ? "unitedStates.IndependenceDay" : null);
    public static final Function<LocalDate, String> LABOR_DAY = e -> e.getMonthValue() == 9 && e.getDayOfMonth() ==
            e.with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.MONDAY)).getDayOfMonth() ? "unitedStates.LaborDay" : null;
    public static final Function<LocalDate, String> VETERANS_DAY = e -> substitution(e, e2 -> e2.getMonthValue() == 11 && e2.getDayOfMonth() == 11 ? "unitedStates.VeteransDay" : null);
    public static final Function<LocalDate, String> THANKS_GIVING_DAY = e -> e.getMonthValue() == 11 && e.getDayOfMonth() ==
            e.with(TemporalAdjusters.dayOfWeekInMonth(4, DayOfWeek.THURSDAY)).getDayOfMonth() ? "unitedStates.ThanksgivingDay" : null;
    public static final Function<LocalDate, String> CHRISTMAS_DAY = e -> substitution(e, e2 -> e2.getMonthValue() == 12 && e2.getDayOfMonth() == 24 ? "unitedStates.ChristmasDay" : null);

    @SuppressWarnings("unchecked")
    public static final Function<LocalDate, String>[] PUBLIC_HOLIDAYS = new Function[]{NEW_YEARS_DAY, MARTIN_LUTHER_KING_JR_DAY,
            MEMORIAL_DAY, INDEPENDENCE_DAY, LABOR_DAY, VETERANS_DAY, THANKS_GIVING_DAY, CHRISTMAS_DAY};

    private static String substitution(LocalDate date, Function<LocalDate, String> logic) {
        final String apply = logic.apply(date);
        if (apply != null) {
            return apply;
        }
        LocalDate movedFrom = null;
        if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            movedFrom = date.minus(1, ChronoUnit.DAYS);
        } else if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
            movedFrom = date.plus(1, ChronoUnit.DAYS);
        }
        if (movedFrom != null) {
            final String originalHoliday = logic.apply(movedFrom);
            if (originalHoliday != null) {
                return "${" + originalHoliday + "} (${unitedStates.observed})";
            }
        }
        return null;
    }
}

