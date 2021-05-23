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

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BusinessCalendarBuilder {
    private boolean built = false;
    final List<Function<LocalDate, String>> holidayLogics = new ArrayList<>();
    private final HolidayMap customHolidayMap = new HolidayMap();
    Locale locale = Locale.getDefault();
    final List<Function<LocalDate, List<BusinessHourSlot>>> businessHours = new ArrayList<>();

    public final BusinessCalendarBuilder locale(Locale locale) {
        ensureNotBuilt();
        holidayLogics.add(customHolidayMap);
        this.locale = locale;
        return this;
    }

    Function<LocalDate, String> holiday() {
        return date -> holidayLogics.stream()
                .map(e -> e.apply(date)).filter(Objects::nonNull).findFirst().orElse(null);
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
    public BusinessCalendarPredicate on(int ordinal, @NotNull DayOfWeek... dayOfWeek) {
        ensureNotBuilt();
        return new BusinessCalendarPredicate(this, ordinal, dayOfWeek);
    }

    @NotNull
    public BusinessCalendarPredicate on(int year, int month, int day) {
        ensureNotBuilt();
        return new BusinessCalendarPredicate(e -> e.getYear() == year && e.getMonthValue() == month && e.getDayOfMonth() == day, this);
    }

    @NotNull
    public BusinessCalendarPredicate on(LocalDate date) {
        ensureNotBuilt();
        return new BusinessCalendarPredicate(date, this);
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


    @NotNull
    Function<LocalDate, List<BusinessHourSlot>> getBusinessHours() {
        return (date) -> {
            for (Function<LocalDate, List<BusinessHourSlot>> bh : businessHours) {
                final List<BusinessHourSlot> apply = bh.apply(date);
                if (apply != null) {
                    return apply;
                }
            }
            return null;
        };
    }

    private void ensureNotBuilt() {
        if (built) {
            throw new IllegalStateException("Already built");
        }
    }

    /**
     * Read CSV configuration file
     * @param path csv file path
     * @return this instance
     * @since 1.15
     */
    public BusinessCalendarBuilder csv(Path path) {
        csv(path, null);
        return this;
    }

    /**
     * Read CSV configuration file
     * @param path csv file path
     * @param duration reload interval
     * @return this instance
     * @since 1.15
     */
    public BusinessCalendarBuilder csv(Path path, Duration duration) {
        CSV csv = new CSV(path, duration);
        this.holidayLogics.add(csv.holiday());
        this.businessHours.add(csv.getBusinessHours());
        return this;
    }

    static class BusinessHours implements Function<LocalDate, List<BusinessHourSlot>>{
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
            businessHourFromTos.sort(Comparator.comparing(e -> e.from));
        }

        @Override
        public List<BusinessHourSlot> apply(LocalDate localDate) {
            if (predicate.test(localDate)) {
                return businessHourFromTos.stream()
                        .map(e -> new BusinessHourSlot(localDate, e.from, e.to)).collect(Collectors.toList());
            }else{
                return null;
            }
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