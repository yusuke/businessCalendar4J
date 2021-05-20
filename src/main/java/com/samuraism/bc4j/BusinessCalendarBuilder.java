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
        return businessHourFrom(hour, 0);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek, int hour) {
        return businessHourFrom(dayOfWeek, hour, 0);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2, int hour) {
        return businessHourFrom(dayOfWeek1, dayOfWeek2, hour, 0);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                                             @NotNull DayOfWeek dayOfWeek3, int hour) {
        return businessHourFrom(dayOfWeek1, dayOfWeek2, dayOfWeek3, hour, 0);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                                             @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4, int hour) {
        return businessHourFrom(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, hour, 0);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                                             @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4,
                                             @NotNull DayOfWeek dayOfWeek5, int hour) {
        return businessHourFrom(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, hour, 0);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                                             @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4,
                                             @NotNull DayOfWeek dayOfWeek5, @NotNull DayOfWeek dayOfWeek6, int hour) {
        return businessHourFrom(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, dayOfWeek6, hour, 0);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                                             @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4,
                                             @NotNull DayOfWeek dayOfWeek5, @NotNull DayOfWeek dayOfWeek6,
                                             @NotNull DayOfWeek dayOfWeek7, int hour) {
        return businessHourFrom(dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, dayOfWeek6, dayOfWeek7, hour, 0);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(int hour, int minutes) {
        return new BusinessHourFrom(new DayOfWeek[0], hour, minutes, this);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek, int hour, int minutes) {
        ensureNotBuilt();
        return new BusinessHourFrom(new DayOfWeek[]{dayOfWeek}, hour, minutes, this);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2, int hour,
                                             int minutes) {
        return new BusinessHourFrom(new DayOfWeek[]{dayOfWeek1, dayOfWeek2}, hour, minutes, this);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                                             @NotNull DayOfWeek dayOfWeek3, int hour, int minutes) {
        return new BusinessHourFrom(new DayOfWeek[]{dayOfWeek1, dayOfWeek2, dayOfWeek3}, hour, minutes, this);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                                             @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4, int hour,
                                             int minutes) {
        return new BusinessHourFrom(new DayOfWeek[]{dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4}, hour, minutes, this);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                                             @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4,
                                             @NotNull DayOfWeek dayOfWeek5, int hour, int minutes) {
        return new BusinessHourFrom(new DayOfWeek[]{dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5}, hour, minutes, this);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                                             @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4,
                                             @NotNull DayOfWeek dayOfWeek5, @NotNull DayOfWeek dayOfWeek6, int hour,
                                             int minutes) {
        return new BusinessHourFrom(new DayOfWeek[]{dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, dayOfWeek6}, hour, minutes, this);
    }

    @NotNull
    public BusinessHourFrom businessHourFrom(@NotNull DayOfWeek dayOfWeek1, @NotNull DayOfWeek dayOfWeek2,
                                             @NotNull DayOfWeek dayOfWeek3, @NotNull DayOfWeek dayOfWeek4,
                                             @NotNull DayOfWeek dayOfWeek5, @NotNull DayOfWeek dayOfWeek6,
                                             @NotNull DayOfWeek dayOfWeek7, int hour, int minutes) {
        return new BusinessHourFrom(new DayOfWeek[]{dayOfWeek1, dayOfWeek2, dayOfWeek3, dayOfWeek4, dayOfWeek5, dayOfWeek6, dayOfWeek7}, hour, minutes, this);
    }

    @NotNull
    Function<LocalDate, List<BusinessHourSlot>> getBusinessHours() {
        return (date) -> {
            if (businessHourFromTos.size() == 0) {
                return Collections.singletonList(new BusinessHourSlot(LocalDateTime.
                        of(date, LocalTime.of(0, 0)),
                        LocalDateTime.of(date.plus(1, ChronoUnit.DAYS), LocalTime.of(0, 0))));
            }
            // Day of week specific algorithm

            if (businessHourFromTos.stream().anyMatch(e -> e.isSpecificTo(date.getDayOfWeek()))) {
                return businessHourFromTos.stream().filter(e -> e.isSpecificTo(date.getDayOfWeek())).map(e -> new BusinessHourSlot(LocalDateTime.
                        of(date, e.from),
                        LocalDateTime.of(date, e.to))).collect(Collectors.toList());

            } else{
                return businessHourFromTos.stream().filter(BusinessHourFromTo::dayOfWeekNotSpecified).map(e -> new BusinessHourSlot(LocalDateTime.
                        of(date, e.from),
                        LocalDateTime.of(date, e.to))).collect(Collectors.toList());
            }
        };
    }

    private void ensureNotBuilt() {
        if (built) {
            throw new IllegalStateException("Already built");
        }
    }

    public class BusinessHourFrom {
        DayOfWeek[] dayOfWeeks;
        int fromHour;
        int fromMinutes;
        BusinessCalendarBuilder builder;

        BusinessHourFrom(DayOfWeek[] dayOfWeeks, int fromHour, int fromMinutes, @NotNull BusinessCalendarBuilder builder) {
            ensureNotBuilt();
            checkParameter(0 <= fromHour, "value should be greater than or equals to 0, provided: " + fromHour);
            checkParameter(fromHour <= 24, "value should be less than or equals to 24, provided: " + fromHour);
            checkParameter(0 <= fromMinutes, "value should be greater than or equals to 0, provided: " + fromMinutes);
            checkParameter(fromMinutes <= 59, "value should be less than 60, provided: " + fromMinutes);
            this.dayOfWeeks = dayOfWeeks;
            this.fromHour = fromHour;
            this.fromMinutes = fromMinutes;
            this.builder = builder;
        }

        @NotNull
        public BusinessCalendarBuilder to(int hour) {
            return to(hour, 0);
        }

        @NotNull
        public BusinessCalendarBuilder to(int hour, int minutes) {
            ensureNotBuilt();
            checkParameter(0 <= hour, "value should be greater than or equals to 0, provided: " + hour);
            checkParameter(hour < 24, "value should be less than 24, provided: " + hour);
            checkParameter(0 <= minutes, "value should be greater than or equals to 0, provided: " + minutes);
            checkParameter(minutes <= 59, "value should be less than 60, provided: " + minutes);
            final LocalTime from = LocalTime.of(fromHour, fromMinutes);
            final LocalTime to = LocalTime.of(hour, minutes);
            checkParameter(from.isBefore(to), "from should be before to, provided: " + from + " / " + to);
            businessHourFromTos.add(new BusinessHourFromTo(LocalTime.of(fromHour, fromMinutes), LocalTime.of(hour, minutes), dayOfWeeks));
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
    @NotNull
    LocalTime from;
    @NotNull
    LocalTime to;
    @NotNull
    DayOfWeek[] dayOfWeeks;

    public BusinessHourFromTo(@NotNull LocalTime from, @NotNull LocalTime to, @NotNull DayOfWeek[] dayOfWeeks) {
        this.from = from;
        this.to = to;
        this.dayOfWeeks = dayOfWeeks;
    }

    boolean isSpecificTo(DayOfWeek dayOfWeek) {
        for (DayOfWeek dow : dayOfWeeks) {
            if (dow == dayOfWeek) {
                return true;
            }
        }
        return false;
    }
    boolean dayOfWeekNotSpecified() {
        return dayOfWeeks.length == 0;
    }
}