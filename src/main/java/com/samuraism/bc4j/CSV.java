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
import java.time.Duration;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

final class CSV {

    private BusinessCalendarBuilder builder;
    @NotNull
    private final Path path;

    private long lastModified = -1L;

    @Nullable
    private final Duration duration;

    CSV(@NotNull Path path, @Nullable Duration duration) {
        this.path = path;
        this.duration = duration;
        reload();
        scheduleReload();
    }

    private void scheduleReload() {
        if (duration != null) {
            final Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(duration.toMillis());
                        reload();
                    } catch (Exception ignore) {
                    }
                }

            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void reload() {
        final long latestLastModified = path.toFile().lastModified();
        if (lastModified != latestLastModified) {
            lastModified = latestLastModified;
            List<String> lines;
            try {
                lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                csv(lines);
            } catch (IOException io) {
                throw new UncheckedIOException(io);
            }
        }
    }

    Function<LocalDate, String> holiday() {
        return date -> builder.holiday().apply(date);
    }

    Function<LocalDate, List<BusinessHourSlot>> getBusinessHours() {
        return date -> builder.getBusinessHours().apply(date);
    }

    void csv(List<String> lines) {
        final BusinessCalendarBuilder newConf = new BusinessCalendarBuilder();
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
                        on(newConf, ymdFormat, mdFormat, split, BusinessCalendarPredicate::hours);
                        break;
                    case "holiday":
                        on(newConf, ymdFormat, mdFormat, split, BusinessCalendarPredicate::holiday);
                        break;
                    default:
                        throw new RuntimeException(new ParseException(line, 0));
                }
            }
        }
        this.builder = newConf;
    }

    private void on(@NotNull BusinessCalendarBuilder newConf, @NotNull DateTimeFormatter ymdFormatter, @NotNull DateTimeFormatter mdFormatter,
                    @NotNull String[] lines, BiConsumer<BusinessCalendarPredicate, String> consumer) {
        // date
        try {
            try {
                final LocalDate date = LocalDate.parse(lines[1], ymdFormatter);
                consumer.accept(new BusinessCalendarPredicate(date, newConf), join(lines, 2));
            } catch (DateTimeParseException dtpe1) {
                final MonthDay parsed = MonthDay.parse(lines[1], mdFormatter);
                consumer.accept(new BusinessCalendarPredicate(date -> date.getMonth() == parsed.getMonth()
                        && date.getDayOfMonth() == parsed.getDayOfMonth(), newConf), join(lines, 2));
            }
        } catch (DateTimeParseException dtpe2) {
            // ordinal
            try {
                parseWeekDays(newConf, lines, 2, Integer.valueOf(lines[1]), consumer);
            } catch (NumberFormatException nfe) {
                parseWeekDays(newConf, lines, 1, null, consumer);
            }

        }
    }

    @SuppressWarnings("serial")
    private static final Map<String, String> convert = new HashMap<String, String>() {{
        put("MON", "MONDAY");
        put("TUE", "TUESDAY");
        put("WED", "WEDNESDAY");
        put("THU", "THURSDAY");
        put("FRI", "FRIDAY");
        put("SAT", "SATURDAY");
        put("SUN", "SUNDAY");
    }};

    private void parseWeekDays(@NotNull BusinessCalendarBuilder newConf, @NotNull String[] lines, int fromIndex, @Nullable Integer ordinal,
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
                consumer.accept(new BusinessCalendarPredicate(e -> true, newConf), buf);
            } else {
                consumer.accept(new BusinessCalendarPredicate(newConf, objects), buf);
            }
        } else {
            consumer.accept(new BusinessCalendarPredicate(newConf, ordinal, objects), buf);
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
}
