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
import java.time.LocalTime;
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
    List<Function<LocalDate, List<BusinessHourSlot>>> businessHourSlotsProvider = new ArrayList<>();
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
        if (businessHourFromTos.size() == 0) {
            businessHourFromTos.add(new BusinessHourFromTo(LocalTime.of(0, 0), LocalTime.of(0, 0), new DayOfWeek[0]));
        }
        final List<BusinessHourFromTo> dayOfWeekSpecified = businessHourFromTos.stream().filter(e -> !e.dayOfWeekNotSpecified()).collect(Collectors.toList());
        if (dayOfWeekSpecified.size() != 0) {
            // Day of week specific algorithm
            businessHourSlotsProvider.add(date -> {
                if (dayOfWeekSpecified.stream().anyMatch(e -> e.isSpecificTo(date.getDayOfWeek()))) {
                    return businessHourFromTos.stream().filter(e -> e.isSpecificTo(date.getDayOfWeek()))
                            .map(e -> new BusinessHourSlot(date, e.from, e.to)).collect(Collectors.toList());
                } else {
                    return null;
                }
            });
        }
        final List<BusinessHourFromTo> dayOfWeekNotSpecified = businessHourFromTos.stream().filter(BusinessHourFromTo::dayOfWeekNotSpecified).collect(Collectors.toList());
        businessHourSlotsProvider.add(date -> dayOfWeekNotSpecified.stream()
                .map(e -> new BusinessHourSlot(date, e.from, e.to)).collect(Collectors.toList()));

        return new BusinessCalendar(this);
    }

    @NotNull
    public BusinessHourFrom from(int hour) {
        return from(hour, 0);
    }

    @NotNull
    public BusinessHourFrom from(int hour, int minutes) {
        return new BusinessHourFrom(new DayOfWeek[0], hour, minutes, this);
    }

    @NotNull
    public BusinessHour on(@NotNull DayOfWeek... dayOfWeek) {
        ensureNotBuilt();
        return new BusinessHour(this, dayOfWeek);
    }

    @NotNull
    Function<LocalDate, List<BusinessHourSlot>> getBusinessHours() {
        return (date) -> {
            List<BusinessHourSlot> slots = null;
            for (Function<LocalDate, List<BusinessHourSlot>> provider : businessHourSlotsProvider) {
                slots = provider.apply(date);
                if (slots != null && slots.size() != 0) {
                    break;
                }
            }
            return slots;
        };
    }

    private void ensureNotBuilt() {
        if (built) {
            throw new IllegalStateException("Already built");
        }
    }

    public class BusinessHour {
        private final BusinessCalendarBuilder builder;
        private final DayOfWeek[] dayOfWeeks;

        BusinessHour(@NotNull BusinessCalendarBuilder builder, @NotNull DayOfWeek... dayOfWeeks) {
            this.builder = builder;
            this.dayOfWeeks = dayOfWeeks;
        }

        @NotNull
        public BusinessHourFrom from(int hour) {
            return from(hour, 0);
        }

        @NotNull
        public BusinessHourFrom from(int hour, int minutes) {
            return new BusinessHourFrom(dayOfWeeks, hour, minutes, builder);
        }
    }

    public class BusinessHourFrom {
        private final DayOfWeek[] dayOfWeeks;
        private final int fromHour;
        private final int fromMinutes;
        private final BusinessCalendarBuilder builder;

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
            checkParameter(hour <= 24, "value should be less than or equals to 24, provided: " + hour);
            checkParameter(0 <= minutes, "value should be greater than or equals to 0, provided: " + minutes);
            checkParameter(minutes <= 59, "value should be less than 60, provided: " + minutes);
            final LocalTime from = LocalTime.of(fromHour, fromMinutes);
            final boolean toIsEndOfTheDay = hour == 24 && minutes == 0;
            final LocalTime to = toIsEndOfTheDay ? LocalTime.of(0, 0) : LocalTime.of(hour, minutes);
            checkParameter(from.isBefore(to) || toIsEndOfTheDay, "from should be before to, provided: " + from + " / " + to);
            businessHourFromTos.add(new BusinessHourFromTo(LocalTime.of(fromHour, fromMinutes), to, dayOfWeeks));
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