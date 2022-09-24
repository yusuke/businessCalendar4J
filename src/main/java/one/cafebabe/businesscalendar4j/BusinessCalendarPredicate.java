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

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * BusinessCalendarPredicate
 */
public class BusinessCalendarPredicate {
    private final BusinessCalendarBuilder builder;
    private final Predicate<LocalDate> predicate;

    BusinessCalendarPredicate(@NotNull BusinessCalendarBuilder builder, int ordinal, @NotNull DayOfWeek... dayOfWeeks) {
        this.predicate = predicate(ordinal, dayOfWeeks);
        this.builder = builder;
    }

    BusinessCalendarPredicate(@NotNull BusinessCalendarBuilder builder, @NotNull DayOfWeek... dayOfWeeks) {
        this.predicate = e -> {
            for (DayOfWeek dayOfWeek : dayOfWeeks) {
                if (dayOfWeek == e.getDayOfWeek()) {
                    return true;
                }
            }
            return false;
        };
        this.builder = builder;
    }

    BusinessCalendarPredicate(@NotNull Predicate<LocalDate> predicate, @NotNull BusinessCalendarBuilder builder) {
        this.predicate = predicate;
        this.builder = builder;
    }

    BusinessCalendarPredicate(@NotNull LocalDate date, @NotNull BusinessCalendarBuilder builder) {
        this.predicate = e -> e.isEqual(date);
        this.builder = builder;
    }

    /**
     * Mark the predicate as a business hour
     * @param businessHour business hour as string
     * @return BusinessCalendarBuilder
     */
    @NotNull
    public BusinessCalendarBuilder hours(@NotNull String businessHour) {
        builder.businessHours.add(new BusinessCalendarBuilder.BusinessHours(predicate, businessHour));
        return builder;
    }

    /**
     * Mark the predicate as a holiday
     * @param name holiday name
     * @return BusinessCalendarBuilder
     */
    @NotNull
    public BusinessCalendarBuilder holiday(@NotNull String name) {
        return builder.holiday(holiday(predicate, name));
    }

    @NotNull
    static Function<LocalDate, String> holiday(@NotNull Predicate<LocalDate> predicate, @NotNull String name) {
        return date -> predicate.test(date) ? name : null;
    }

    @NotNull
    static Predicate<LocalDate> predicate(int month, int day) {
        return e -> e.getMonthValue() == month && e.getDayOfMonth() == day;
    }

    @NotNull
    static Predicate<LocalDate> predicate(int ordinal, @NotNull DayOfWeek dayOfWeek, int month) {
        return e -> e.getMonthValue() == month && dayOfWeekOrdinalMatches(e, ordinal, dayOfWeek);
    }

    @NotNull
    static Predicate<LocalDate> predicate(int ordinal, @NotNull DayOfWeek[] dayOfWeeks) {
        return e -> {
            for (DayOfWeek dayOfWeek : dayOfWeeks) {
                if (dayOfWeek == e.getDayOfWeek()) {
                    if (dayOfWeekOrdinalMatches(e, ordinal, dayOfWeek)) return true;
                }
            }
            return false;
        };
    }

    static boolean dayOfWeekOrdinalMatches(LocalDate e, int ordinal, DayOfWeek dayOfWeek) {
        int day = e.with(TemporalAdjusters
                .dayOfWeekInMonth(ordinal, dayOfWeek))
                .getDayOfMonth();
        return e.getDayOfMonth() == day;
    }
}
