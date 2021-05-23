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

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Predicate;

public class BusinessCalendarPredicate {
    private final BusinessCalendarBuilder builder;
    private final Predicate<LocalDate> predicate;

    BusinessCalendarPredicate(@NotNull BusinessCalendarBuilder builder, int ordinal, @NotNull DayOfWeek... dayOfWeeks) {
        this.predicate = e -> {
            for (DayOfWeek dayOfWeek : dayOfWeeks) {
                if (dayOfWeek == e.getDayOfWeek()) {
                    int day = e.with(TemporalAdjusters
                            .dayOfWeekInMonth(ordinal, dayOfWeek))
                            .getDayOfMonth();
                    if (e.getDayOfMonth() == day) {
                        return true;
                    }
                }
            }
            return false;
        };
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

    public BusinessCalendarBuilder hours(String businessHour) {
        builder.businessHours.add(new BusinessCalendarBuilder.BusinessHours(predicate, businessHour));
        return builder;
    }

    public BusinessCalendarBuilder holiday(String name) {
        return builder.holiday(date -> predicate.test(date) ? name : null);
    }
}
