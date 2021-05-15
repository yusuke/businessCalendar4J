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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BusinessCalendarBuilder {
    boolean built = false;
    List<Function<LocalDate, String>> holidayLogics = new ArrayList<>();
    HolidayMap customHolidayMap = new HolidayMap();
    Locale locale = Locale.getDefault();
    List<BusinessHourFromTo> businessHourFromTos = new ArrayList<>();

    public final BusinessCalendarBuilder locale(Locale locale) {
        ensureNotBuilt();
        this.locale = locale;
        return this;
    }

    /**
     * Add logic based holiday(s).
     *
     * @param logics logics
     * @return This instance
     */
    @SafeVarargs
    public final BusinessCalendarBuilder holiday(Function<LocalDate, String>... logics) {
        ensureNotBuilt();
        Collections.addAll(holidayLogics, logics);
        return this;
    }

    /**
     * Add fixed holiday
     *
     * @param date date
     * @param name name
     * @return This instance
     */
    @NotNull
    public BusinessCalendarBuilder holiday(@NotNull LocalDate date, @NotNull String name) {
        ensureNotBuilt();
        customHolidayMap.addHoliday(date, name);
        return this;
    }

    @NotNull
    public BusinessCalendar build() {
        ensureNotBuilt();
        built = true;
        return new BusinessCalendar(this);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(int hour) {
        ensureNotBuilt();
        return businessHourFrom(hour, 0);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(int hour, int minutes) {
        ensureNotBuilt();
        return new BusinessHourFrom(hour, minutes, this);
    }

    @NotNull
    Function<LocalDate ,List<BusinessHourSlot>> getBusinessHours() {
        return (date)-> {
            if (businessHourFromTos.size() == 0) {
                return Collections.singletonList(new BusinessHourSlot(LocalDateTime.
                        of(date, LocalTime.of(0, 0)),
                        LocalDateTime.of(date.plus(1, ChronoUnit.DAYS), LocalTime.of(0, 0))));
            }
            return businessHourFromTos.stream().map(e -> new BusinessHourSlot(LocalDateTime.
                    of(date, e.from),
                    LocalDateTime.of(date, e.to))).collect(Collectors.toList());
        };
    }
    private void ensureNotBuilt(){
        if (built) {
            throw new IllegalStateException("Already built");
        }
    }

    class BusinessHourFrom {
        int fromHour;
        int fromMinutes;
        BusinessCalendarBuilder builder;

        BusinessHourFrom(int fromHour, int fromMinutes, @NotNull BusinessCalendarBuilder builder) {
            checkParameter(0 <= fromHour, "value should be greater than or equals to 0, provided: " + fromHour);
            checkParameter(fromHour <= 24, "value should be less than or equals to 24, provided: " + fromHour);
            checkParameter(0 <= fromMinutes, "value should be greater than or equals to 0, provided: " + fromMinutes);
            checkParameter(fromMinutes <= 59, "value should be less than 60, provided: " + fromMinutes);
            this.fromHour = fromHour;
            this.fromMinutes = fromMinutes;
            this.builder = builder;
        }

        @NotNull
        BusinessCalendarBuilder to(int hour) {
            return to(hour, 0);
        }

        @NotNull
        BusinessCalendarBuilder to(int hour, int minutes) {
            ensureNotBuilt();
            checkParameter(0 <= hour, "value should be greater than or equals to 0, provided: " + hour);
            checkParameter(hour < 24, "value should be less than 24, provided: " + hour);
            checkParameter(0 <= minutes, "value should be greater than or equals to 0, provided: " + minutes);
            checkParameter(minutes <= 59, "value should be less than 60, provided: " + minutes);
            final LocalTime from = LocalTime.of(fromHour, fromMinutes);
            final LocalTime to = LocalTime.of(hour, minutes);
            checkParameter(from.isBefore(to), "from should be before to, provided: " + from + " / " + to);
            businessHourFromTos.add(new BusinessHourFromTo(LocalTime.of(fromHour, fromMinutes), LocalTime.of(hour, minutes)));
            return builder;
        }

        void checkParameter(boolean expectedToBeTrue, @NotNull String message) {
            if (!expectedToBeTrue) {
                throw new IllegalArgumentException(message);
            }
        }
    }
}

class BusinessHourFromTo {
    LocalTime from;
    LocalTime to;

    public BusinessHourFromTo(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }
}