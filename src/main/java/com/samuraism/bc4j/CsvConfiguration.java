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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class CsvConfiguration {

    private final Logger logger = Logger.getLogger();

    private BusinessCalendarBuilder builder;
    @Nullable
    private final Path path;

    @Nullable
    private final URL url;

    private long lastModified = -1L;

    /**
     * Creates a CSV configuration from file path
     *
     * @param path configuration location
     * @return configuration instance
     * @since 1.18
     */
    public static CsvConfiguration getInstance(@NotNull Path path) {
        return new CsvConfiguration(path);
    }

    /**
     * Creates a CSV configuration from URL
     *
     * @param url configuration location
     * @return configuration instance
     * @since 1.18
     */
    public static CsvConfiguration getInstance(@NotNull URL url) {
        return new CsvConfiguration(url);
    }

    private CsvConfiguration(@NotNull Path path) {
        this.path = path;
        this.url = null;
        reload();
    }

    private CsvConfiguration(@NotNull URL url) {
        this.path = null;
        this.url = url;
        reload();
    }

    private boolean reloadScheduled = false;

    void scheduleReload(@Nullable Duration interval) {
        if (reloadScheduled) {
            throw new IllegalStateException("reload already scheduled");
        }
        if (interval != null) {
            reloadScheduled = true;
            final Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(interval.toMillis());
                        reload();
                    } catch (Exception ignore) {
                    }
                }

            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * reload configuration file
     *
     * @return warning messages
     * @since 1.18
     */
    public List<String> reload() {
        List<String> messages = new ArrayList<>();

        if (path != null) {

            final File file = path.toFile();
            if (!file.exists()) {
                final String message = path.toAbsolutePath() + " does not exist";
                messages.add(message);
                logger.warn(() -> message);
            }
            final long latestLastModified = file.lastModified();

            if (lastModified == latestLastModified) {
                logger.debug(() -> path.toAbsolutePath() + " is not modified");
            }
            lastModified = latestLastModified;
            List<String> lines;
            try {
                logger.info(() -> "loading: " + path.toAbsolutePath());
                lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                messages.addAll(csv(lines));
            } catch (IOException io) {
                final String message = "failed to load: " + path.toAbsolutePath();
                messages.add(message);
                logger.warn(() -> message, io);
            }
        }
        if (url != null) {
            final URLConnection con;
            try {
                logger.info(() -> "loading: " + url);
                con = url.openConnection();
                con.setReadTimeout((int) Duration.of(1, ChronoUnit.MINUTES).toMillis());
                con.setConnectTimeout((int) Duration.of(30, ChronoUnit.SECONDS).toMillis());
                con.connect();
                ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
                try (InputStream is = con.getInputStream()) {
                    int read;
                    while (-1 != (read = is.read())) {
                        out.write(read);
                    }
                }
                String content = out.toString("UTF8");

                final List<String> lines = Arrays.asList(content.split("\n"));
                messages.addAll(csv(lines));
            } catch (IOException e) {
                final String message = "failed to connect: " + url;
                messages.add(message);
                logger.warn(() -> message, e);
            }
        }
        return messages;
    }

    Function<LocalDate, String> holiday() {
        return date -> builder.holiday().apply(date);
    }

    Function<LocalDate, List<BusinessHourSlot>> getBusinessHours() {
        return date -> builder.getBusinessHours().apply(date);
    }

    List<String> csv(List<String> lines) {
        List<String> warnings = new ArrayList<>();
        final BusinessCalendarBuilder newConf = new BusinessCalendarBuilder();
        DateTimeFormatter ymdFormat = DateTimeFormatter.ofPattern("yyyy/M/d");
        DateTimeFormatter mdFormat = DateTimeFormatter.ofPattern("M/d");
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("#")) {
                continue;
            }
            final String[] split = line.split(",");
            try {
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
                            final String message = "Skipping line[" + (i + 1) + "] (unable to parse): \"" + line + "\"";
                            warnings.add(message);
                            logger.warn(() -> message);
                    }
                }
            } catch (Exception e) {
                final String message = "Skipping line[" + (i + 1) + "] (unable to parse): \"" + line + "\"";
                warnings.add(message);
                logger.warn(() -> message);
            }
        }
        this.builder = newConf;
        return warnings;
    }

    private void on(@NotNull BusinessCalendarBuilder newConf, @NotNull DateTimeFormatter ymdFormatter, @NotNull DateTimeFormatter mdFormatter,
                    @NotNull String[] lines, BiConsumer<BusinessCalendarPredicate, String> consumer) {
        // date
        try {
            try {
                final LocalDate date = LocalDate.parse(lines[1], ymdFormatter);
                consumer.accept(new BusinessCalendarPredicate(date, newConf), join(lines, 2));
            } catch (DateTimeParseException e1) {
                final MonthDay parsed = MonthDay.parse(lines[1], mdFormatter);
                consumer.accept(new BusinessCalendarPredicate(date -> date.getMonth() == parsed.getMonth()
                        && date.getDayOfMonth() == parsed.getDayOfMonth(), newConf), join(lines, 2));
            }
        } catch (DateTimeParseException e2) {
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
