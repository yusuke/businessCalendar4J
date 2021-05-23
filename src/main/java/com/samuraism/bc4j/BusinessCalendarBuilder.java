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
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BusinessCalendarBuilder {
    private boolean built = false;
    final List<Function<LocalDate, String>> holidayLogics = new ArrayList<>();
    private final HolidayMap customHolidayMap = new HolidayMap();
    Locale locale = Locale.getDefault();
    private final List<Function<LocalDate, List<BusinessHourSlot>>> businessHours = new ArrayList<>();

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

    public BusinessCalendarBuilder csv(Path path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            final BusinessCalendarBuilder nestedCsvConfiguration = new BusinessCalendarBuilder();
            nestedCsvConfiguration.csv(lines);
            this.holidayLogics.add(nestedCsvConfiguration.holiday());
            this.businessHours.add(nestedCsvConfiguration.getBusinessHours());
        } catch (IOException io) {
            throw new UncheckedIOException(io);
        }
        return this;

    }

    public void csv(List<String> lines) {
        DateTimeFormatter ymdFormat = DateTimeFormatter.ofPattern("yyyy/M/d");
        DateTimeFormatter mdFormat = DateTimeFormatter.ofPattern("M/d");
        for (String line : lines) {
            if (line.startsWith("#")) {
                continue;
            }
            final String[] split = line.split(",");
            if (1 <= split.length) {
                switch (split[0]) {
                    case "ymdFormat":
                        ymdFormat = DateTimeFormatter.ofPattern(split[1]);
                        break;
                    case "mdFormat":
                        mdFormat = DateTimeFormatter.ofPattern(split[1]);
                        break;
                    case "hours":
                        on(ymdFormat, mdFormat, split, BusinessCalendarPredicate::hours);
                        break;
                    case "holiday":
                        on(ymdFormat, mdFormat, split, BusinessCalendarPredicate::holiday);
                        break;
                    default:
                        throw new RuntimeException(new ParseException(line, 0));
                }
            }
        }
    }

    private void on(@NotNull DateTimeFormatter ymdFormatter, @NotNull DateTimeFormatter mdFormatter,
                    @NotNull String[] lines, BiConsumer<BusinessCalendarPredicate, String> consumer) {
        // date
        try {
            try {
                final LocalDate date = LocalDate.parse(lines[1], ymdFormatter);
                consumer.accept(new BusinessCalendarPredicate(date, this), join(lines, 2));
            } catch (DateTimeParseException dtpe1) {
                final MonthDay parsed = MonthDay.parse(lines[1], mdFormatter);
                consumer.accept(new BusinessCalendarPredicate(date -> date.getMonth() == parsed.getMonth()
                        && date.getDayOfMonth() == parsed.getDayOfMonth(), this), join(lines, 2));
            }
        } catch (DateTimeParseException dtpe2) {
            // ordinal
            try {
                parseWeekDays(lines, 2, Integer.valueOf(lines[1]), consumer);
            } catch (NumberFormatException nfe) {
                parseWeekDays(lines, 1, null, consumer);
            }

        }
    }


    @SuppressWarnings("serial")
    static Map<String, String> convert = new HashMap<String, String>() {{
        put("MON", "MONDAY");
        put("TUE", "TUESDAY");
        put("WED", "WEDNESDAY");
        put("THU", "THURSDAY");
        put("FRI", "FRIDAY");
        put("SAT", "SATURDAY");
        put("SUN", "SUNDAY");
    }};

    private void parseWeekDays(@NotNull String[] lines, int fromIndex, @Nullable Integer ordinal,
                               BiConsumer<BusinessCalendarPredicate, String> consumer) {
        List<DayOfWeek> dayOfWeeks = new ArrayList<>();
        while ((fromIndex) < lines.length) {
            final String uppercase = lines[fromIndex].toUpperCase(Locale.ENGLISH);
            try {
                dayOfWeeks.add(DayOfWeek.valueOf(convert.getOrDefault(uppercase, uppercase)));
            } catch (IllegalArgumentException notDayOfWeek) {
                break;
            }
            fromIndex++;
        }
        String buf = join(lines, fromIndex);
        final DayOfWeek[] objects = dayOfWeeks.toArray(new DayOfWeek[0]);
        if (ordinal == null) {
            if (dayOfWeeks.size() == 0) {
                consumer.accept(new BusinessCalendarPredicate(e -> true, this), buf);
            } else {
                consumer.accept(new BusinessCalendarPredicate(this, objects), buf);
            }
        } else {
            consumer.accept(new BusinessCalendarPredicate(this, ordinal, objects), buf);
        }
    }

    private String join(String[] split, int fromIndex) {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (int i = fromIndex; i < split.length; i++) {
            if (!first) {
                buf.append(",");
            }
            buf.append(split[i]);
            first = false;
        }
        return buf.toString();
    }

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
            businessHours.add(new BusinessHours(predicate, businessHour));
            return builder;
        }

        public BusinessCalendarBuilder holiday(String name) {
            return builder.holiday(date -> predicate.test(date) ? name : null);
        }
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