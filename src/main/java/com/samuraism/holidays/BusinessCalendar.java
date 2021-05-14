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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class BusinessCalendar {
    private final List<Function<LocalDate, String>> holidayLogics = new ArrayList<>();
    private final BusinessHours businessHours;

    private final ResourceBundle resource;

    BusinessCalendar(BusinessCalendarBuilder conf) {
        this.resource = ResourceBundle.getBundle("holidays", conf.locale);
        holidayLogics.addAll(conf.holidayLogics);
        this.holidayLogics.add(conf.customHolidayMap);
        this.businessHours = conf.getBusinessHours();
    }

    @NotNull
    public static BusinessCalendarBuilder newBuilder() {
        return new BusinessCalendarBuilder();
    }

    /**
     * Fixed algorithm to close on Saturdays and Sundays
     *
     * @since 1.5
     */
    public static final Function<LocalDate, String> CLOSED_ON_SATURDAYS_AND_SUNDAYS = localDate -> {
        switch (localDate.getDayOfWeek()) {
            case SATURDAY:
                return "japanese.土曜日";
            case SUNDAY:
                return "japanese.日曜日";
            default:
                return null;
        }
    };

    /**
     * Test if the specified date is a holiday
     *
     * @param date date
     * @return true if the specified date is a holiday
     */
    public boolean isHoliday(@NotNull LocalDate date) {
        return holidayLogics.stream().anyMatch(e -> e.apply(date) != null);
    }

    /**
     * Test if today is a holiday
     *
     * @return true if the specified date is a holiday
     * @since 1.3
     */
    public boolean isHoliday() {
        final LocalDate today = LocalDate.now();
        return holidayLogics.stream().anyMatch(e -> e.apply(today) != null);
    }

    /**
     * Test if the specified date is a business day
     *
     * @param date date
     * @return true if the specified date is a business day
     */
    public boolean isBusinessDay(@NotNull LocalDate date) {
        return !isHoliday(date);
    }

    /**
     * Test if today is a business day
     *
     * @return true if the specified date is a business day
     * @since 1.3
     */
    public boolean isBusinessDay() {
        return !isHoliday(LocalDate.now());
    }

    /**
     * Test if specified time is during business hours
     *
     * @param dateTime time
     * @return true is specified time is during business hours
     * @since 1.8
     */
    public boolean isBusinessHour(@NotNull LocalDateTime dateTime) {
        return businessHours.getSlots(dateTime.toLocalDate()).stream().anyMatch(e -> e.isBusinessHour(dateTime));
    }

    /**
     * Test if it's during business hours
     *
     * @return true is it's during business hours
     * @since 1.8
     */
    public boolean isBusinessHour() {
        return isBusinessHour(LocalDateTime.now());
    }

    /**
     * Returns when last business hours ended
     *
     * @param when origin
     * @return the time when last business hours ended
     * @since 1.8
     */
    @NotNull
    public LocalDateTime lastBusinessHourEnd(@NotNull LocalDateTime when) {
        final LocalDate date = when.toLocalDate();
        LocalDateTime lastBusinessHourEnd = null;
        if (isBusinessDay(date)) {
            final List<BusinessHourSlot> slots = businessHours.getSlots(date);
            final List<BusinessHourSlot> list = slots.stream().filter(e -> e.to.isBefore(when) || e.to.isEqual(when)).collect(Collectors.toList());
            if (0 < list.size()) {
                lastBusinessHourEnd = list.get(list.size() - 1).to;
            }

        }
        if (lastBusinessHourEnd == null) {
            final List<BusinessHourSlot> slots = businessHours.getSlots(lastBusinessDay(date.minus(1, ChronoUnit.DAYS)));
            lastBusinessHourEnd = slots.get(slots.size() - 1).to;
        }
        return lastBusinessHourEnd;
    }

    /**
     * Returns when next business hours end
     *
     * @param when origin
     * @return the time when when next business hours end
     * @since 1.8
     */
    @NotNull
    public LocalDateTime nextBusinessHourEnd(@NotNull LocalDateTime when) {
        final LocalDate date = when.toLocalDate();
        LocalDateTime nextBusinessHourEnd = null;
        if (isBusinessDay(date)) {
            final List<BusinessHourSlot> slots = businessHours.getSlots(date);
            final List<BusinessHourSlot> list = slots.stream().filter(e -> e.to.isAfter(when) || e.to.isEqual(when)).collect(Collectors.toList());
            if (0 < list.size()) {
                nextBusinessHourEnd = list.get(0).to;
            }

        }
        if (nextBusinessHourEnd == null) {
            final List<BusinessHourSlot> slots = businessHours.getSlots(firstBusinessDay(date.plus(1, ChronoUnit.DAYS)));
            nextBusinessHourEnd = slots.get(0).to;
        }

        return nextBusinessHourEnd;
    }

    /**
     * Returns when last business hours started
     *
     * @param when origin
     * @return the time when last business hours started
     * @since 1.8
     */
    @NotNull
    public LocalDateTime lastBusinessHourStart(@NotNull LocalDateTime when) {
        final LocalDate date = when.toLocalDate();
        LocalDateTime lastBusinessHourStart = null;
        if (isBusinessDay(date)) {
            final List<BusinessHourSlot> slots = businessHours.getSlots(date);
            final List<BusinessHourSlot> list = slots.stream().filter(e -> e.from.isBefore(when)).collect(Collectors.toList());
            if (0 < list.size()) {
                lastBusinessHourStart = list.get(list.size() - 1).from;
            }

        }
        if (lastBusinessHourStart == null) {
            final List<BusinessHourSlot> slots = businessHours.getSlots(lastBusinessDay(date.minus(1, ChronoUnit.DAYS)));
            lastBusinessHourStart = slots.get(slots.size() - 1).from;
        }
        return lastBusinessHourStart;
    }

    /**
     * Returns when next business hours start
     *
     * @param when origin
     * @return the time when when next business hours start
     * @since 1.8
     */
    @NotNull
    public LocalDateTime nextBusinessHourStart(@NotNull LocalDateTime when) {
        final LocalDate date = when.toLocalDate();
        LocalDateTime nextBusinessHourStart = null;
        if (isBusinessDay(date)) {
            final List<BusinessHourSlot> slots = businessHours.getSlots(date);
            final List<BusinessHourSlot> list = slots.stream().filter(e -> e.from.isAfter(when) || e.from.isEqual(when)).collect(Collectors.toList());
            if (0 < list.size()) {
                nextBusinessHourStart = list.get(0).from;
            }

        }
        if (nextBusinessHourStart == null) {
            final List<BusinessHourSlot> slots = businessHours.getSlots(firstBusinessDay(date.plus(1, ChronoUnit.DAYS)));
            nextBusinessHourStart = slots.get(0).from;
        }

        return nextBusinessHourStart;
    }

    /**
     * Returns a Holiday on the specified date
     *
     * @param date date
     * @return Holiday
     */
    @Nullable
    public Holiday getHoliday(@NotNull LocalDate date) {
        final Optional<String> first = holidayLogics.stream()
                .map(e -> e.apply(date)).filter(Objects::nonNull).findFirst();
        return first.map(s -> new Holiday(date, toHolidayString(s))).orElse(null);
    }

    Pattern p = Pattern.compile("\\$\\{([a-z.A-Z]+)}");
    @NotNull
    private String toHolidayString(@NotNull String key) {
        if (resource.containsKey(key)) {
            return resource.getString(key);
        }
        final Matcher matcher = p.matcher(key);
        StringBuilder b = new StringBuilder();
        int start = 0;
        while (matcher.find()) {
            b.append(key, start, matcher.start());
            final String group = matcher.group(1);
            b.append(resource.getString(group));
            start = matcher.end();
        }
        b.append(key, start, key.length());
        return b.toString();
    }

    /**
     * Returns the last business day by a specific date
     *
     * @param date specific date
     * @return last business day by the specified date
     */
    @NotNull
    public LocalDate lastBusinessDay(@NotNull LocalDate date) {
        LocalDate check = date;
        while (isHoliday(check)) {
            check = check.minus(1, ChronoUnit.DAYS);
        }
        return check;
    }

    /**
     * Returns the last business day by today
     *
     * @return last business day by the specified date
     * @since 1.4
     */
    @NotNull
    public LocalDate lastBusinessDay() {
        return lastBusinessDay(LocalDate.now());
    }

    /**
     * Returns the first business day on or after a specific date
     *
     * @param date specific date
     * @return first business day on and after the specified date
     */
    @NotNull
    public LocalDate firstBusinessDay(@NotNull LocalDate date) {
        LocalDate check = date;
        while (isHoliday(check)) {
            check = check.plus(1, ChronoUnit.DAYS);
        }
        return check;
    }

    /**
     * Returns the first business day after today
     *
     * @return first business day after today
     * @since 1.4
     */
    @NotNull
    public LocalDate firstBusinessDay() {
        return firstBusinessDay(LocalDate.now());
    }

    /**
     * Returns the last holiday by a specific date
     *
     * @param date specific date
     * @return last holiday by the specified date
     */
    @NotNull
    public Holiday lastHoliday(@NotNull LocalDate date) {
        LocalDate check = date;
        while (!isHoliday(check)) {
            if (check.equals(LocalDate.MIN)) {
                return new Holiday(LocalDate.MIN, "min");
            }
            check = check.minus(1, ChronoUnit.DAYS);
        }
        return Objects.requireNonNull(getHoliday(check));
    }

    /**
     * Returns the last holiday by today
     *
     * @return last holiday by today
     * @since 1.4
     */
    @NotNull
    public Holiday lastHoliday() {
        return lastHoliday(LocalDate.now());
    }

    /**
     * Returns the first holiday on or after a specific date
     *
     * @param date specific date
     * @return first holiday on or after the specified date
     */
    @NotNull
    public Holiday firstHoliday(@NotNull LocalDate date) {
        LocalDate check = date;
        while (!isHoliday(check)) {
            if (check.equals(LocalDate.MAX)) {
                return new Holiday(LocalDate.MAX, "max");
            }
            check = check.plus(1, ChronoUnit.DAYS);
        }
        return Objects.requireNonNull(getHoliday(check));
    }

    /**
     * Returns the first holiday after today
     *
     * @return first holiday on or after the specified date
     */
    @NotNull
    public Holiday firstHoliday() {
        return firstHoliday(LocalDate.now());
    }

    /**
     * Returns holidays between specified period
     *
     * @param from from date (inclusive)
     * @param to   to date (inclusive)
     * @return List of holidays between the specified period
     */
    @NotNull
    public List<Holiday> getHolidaysBetween️(@NotNull LocalDate from, @NotNull LocalDate to) {
        List<Holiday> list = new ArrayList<>();
        LocalDate start = from.isBefore(to) ? from : to;
        LocalDate end = (to.isAfter(from) ? to : from).plus(1, ChronoUnit.DAYS);
        while (start.isBefore(end)) {
            final Holiday holiday = getHoliday(start);
            if (holiday != null) {
                list.add(holiday);
            }
            start = start.plus(1, ChronoUnit.DAYS);
        }
        return list;
    }

}
