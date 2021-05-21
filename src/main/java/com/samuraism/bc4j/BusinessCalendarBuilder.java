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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BusinessCalendarBuilder {
    private boolean built = false;
    List<Function<LocalDate, String>> holidayLogics = new ArrayList<>();
    HolidayMap customHolidayMap = new HolidayMap();
    Locale locale = Locale.getDefault();
    private List<BusinessHours> businessHours = new ArrayList<>();

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

    @NotNull
    public BusinessCalendar build() {
        ensureNotBuilt();
        built = true;
        return new BusinessCalendar(this);
    }

    @NotNull
    public BusinessCalendarBuilder hours(String businessHour) {
        businessHours.add(new BusinessHours(e -> true, businessHour));
        return this;
    }

    @NotNull
    public BusinessCalendarPredicate on(@NotNull DayOfWeek... dayOfWeek) {
        ensureNotBuilt();
        return new BusinessCalendarPredicate(this, dayOfWeek);
    }

    @NotNull
    public BusinessCalendarPredicate on(int year, int month, int day) {
        ensureNotBuilt();
        return new BusinessCalendarPredicate(e -> e.getYear() == year && e.getMonthValue() == month && e.getDayOfMonth() == day, this);
    }

    @NotNull
    public BusinessCalendarPredicate on(int month, int day) {
        ensureNotBuilt();
        return new BusinessCalendarPredicate(e -> e.getMonthValue() == month && e.getDayOfMonth() == day, this);
    }

    @NotNull
    public BusinessCalendarPredicate on(@NotNull Predicate<LocalDate> predicate) {
        ensureNotBuilt();
        return new BusinessCalendarPredicate(predicate, this);
    }


    private final BusinessHours OPEN24HOURS = new BusinessHours(e -> true, "0-24");

    @NotNull
    Function<LocalDate, List<BusinessHourSlot>> getBusinessHours() {
        return (date) -> {
            for (BusinessHours bh : businessHours) {
                if (bh.predicate.test(date)) {
                    return bh.getSlots(date);
                }
            }
            return OPEN24HOURS.getSlots(date);
        };
    }

    private void ensureNotBuilt() {
        if (built) {
            throw new IllegalStateException("Already built");
        }
    }

    public class BusinessCalendarPredicate {
        private final BusinessCalendarBuilder builder;
        private final Predicate<LocalDate> predicate;

        BusinessCalendarPredicate(BusinessCalendarBuilder builder, DayOfWeek... dayOfWeeks) {
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

        BusinessCalendarPredicate(Predicate<LocalDate> predicate, BusinessCalendarBuilder builder) {
            this.predicate = predicate;
            this.builder = builder;
        }

        public BusinessCalendarBuilder hours(String businessHour) {
            businessHours.add(new BusinessHours(predicate, businessHour));
            return builder;
        }

        public BusinessCalendarBuilder holiday(String name) {
            return builder.holiday(date -> predicate.test(date) ? name : null);
        }
    }

    static class BusinessHours {
        private final Predicate<LocalDate> predicate;
        private final List<BusinessHourFromTo> businessHourFromTos = new ArrayList<>();

        public BusinessHours(Predicate<LocalDate> predicate, String businessHour) {
            this.predicate = predicate;
            final String[] slots = businessHour.replaceAll(" ", "").replaceAll("[、&]", ",").split(",");

            for (String slot : slots) {
                final String[] split = slot.replaceAll("(to|から|〜|~)", "-").split("-");
                final LocalTime from = toLocalTime(split[0]);
                final LocalTime to = toLocalTime(split[1]);
                checkParameter(from.isBefore(to) || to.equals(LocalTime.of(0, 0)), "from should be before to, provided: " + slot);
                businessHourFromTos.add(new BusinessHourFromTo(from, to));
            }
        }

        public List<BusinessHourSlot> getSlots(LocalDate date) {
            return businessHourFromTos.stream()
                    .map(e -> new BusinessHourSlot(date, e.from, e.to)).collect(Collectors.toList());
        }

        private LocalTime toLocalTime(String timeStr) {
            final String ampm = timeStr.replaceAll("[0-9.:時半]", "").toLowerCase();
            final boolean half = timeStr.contains("半");
            timeStr = timeStr.replaceAll("[^0-9:]", "");
            final String[] split = timeStr.split(":");

            int hour = ampm.matches("(noon|正午)") ? 12 : Integer.parseInt(split[0]);
            if (ampm.matches("(a|am|午前)") && hour == 12) {
                hour = 0;
            }
            if (ampm.matches("(p|pm|午後)") && hour != 12) {
                hour += 12;
            }
            if (ampm.matches("midnight")) {
                hour = 24;
            }

            int minutes = 0;
            if (2 <= split.length) {
                minutes = Integer.parseInt(split[1]);
            }
            if (half) {
                minutes = 30;
            }
            int seconds = 0;
            if (3 <= split.length) {
                seconds = Integer.parseInt(split[2]);
            }
            if (hour == 24 && minutes == 0 && seconds == 0) {
                return LocalTime.of(0, 0);
            }

            checkParameter(0 <= hour, "hour should be greater than or equals to 0, provided: " + timeStr);
            checkParameter(hour <= 24, "hour should be less than or equals to 24, provided: " + timeStr);

            checkParameter(0 <= minutes, "minutes should be greater than or equals to 0, provided: " + timeStr);
            checkParameter(minutes <= 59, "minutes should be less than 60, provided: " + timeStr);

            checkParameter(0 <= seconds, "seconds should be greater than or equals to 0, provided: " + timeStr);
            checkParameter(seconds <= 59, "seconds should be less than 60, provided: " + timeStr);

            return LocalTime.of(hour, minutes, seconds);
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

    BusinessHourFromTo(@NotNull LocalTime from, @NotNull LocalTime to) {
        this.from = from;
        this.to = to;
    }
}