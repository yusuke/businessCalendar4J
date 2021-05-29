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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
class CSVBasedConfiguration {
    @Test
    void invalidFormat() throws IOException {
        TestHandler.init();
        final Path doesnotexist = Paths.get("doesnotexist");
        BusinessCalendar.newBuilder().csv(doesnotexist).build();
        assertTrue(TestHandler.logMessages.contains(doesnotexist.toAbsolutePath() + " does not exist"));
        {
            // hour should be hours
            final Path path = write("hour,sat,13-17");
            BusinessCalendar.newBuilder().csv(path).build();
        }
        assertTrue(TestHandler.logMessages.contains("Skipping line[1] (unable to parse): \"hour,sat,13-17\""));

        {
            // saturday should be saturday
            final Path path = write("\nhours,sataday,13-17");
            BusinessCalendar.newBuilder().csv(path).build();
        }
        assertTrue(TestHandler.logMessages.contains("Skipping line[2] (unable to parse): \"hours,sataday,13-17\""));
    }

    @Test
    void reload() throws IOException, InterruptedException {
        final Path path = write(
                "holiday,2021/12/24,just holiday\n"
        );
        final BusinessCalendar expected1 = BusinessCalendar.newBuilder().on(2021, 12, 24).holiday("just holiday").build();

        final BusinessCalendar calendar1 = BusinessCalendar.newBuilder().csv(path, Duration.of(100, ChronoUnit.MILLIS)).build();
        assertCal(expected1, calendar1);
        Thread.sleep(1000);

        write(path, "holiday,2021/11/24,just holiday\n");
        Thread.sleep(3000);
        final BusinessCalendar expected2 = BusinessCalendar.newBuilder().on(2021, 11, 24).holiday("just holiday").build();
        assertCal(expected2, calendar1);
    }

    @Test
    void classPath() {
        final BusinessCalendar cal = BusinessCalendar.newBuilder()
                .csv(CSVBasedConfiguration.class.getResource("/csvconf.csv")).build();
        assertTrue(cal.isHoliday(LocalDate.of(2021, 12, 20)));
        assertTrue(cal.isHoliday(LocalDate.of(2022, 11, 24)));
        assertEquals(2, (long) cal.getHolidaysBetween(LocalDate.of(2021, 1, 1), 
                LocalDate.of(2022, 12, 31)).size());
    }

    @Test
    void fileGotDeleted() throws IOException, InterruptedException {
        final Path path = write(
                "holiday,2021/12/24,just holiday\n"
        );
        final BusinessCalendar expected1 = BusinessCalendar.newBuilder().on(2021, 12, 24).holiday("just holiday").build();

        final BusinessCalendar calendar1 = BusinessCalendar.newBuilder().csv(path, Duration.of(100, ChronoUnit.MILLIS)).build();
        assertCal(expected1, calendar1);
        Thread.sleep(1000);
        //noinspection ResultOfMethodCallIgnored
        path.toFile().delete();

        Thread.sleep(3000);
        assertCal(expected1, calendar1);
    }

    @Test
    void emptyConfigurationFileWillEmptyConfiguration() throws IOException, InterruptedException {
        final Path path = write(
                "holiday,2021/12/24,just holiday\n"
        );
        final BusinessCalendar expected1 = BusinessCalendar.newBuilder().on(2021, 12, 24).holiday("just holiday").build();

        final BusinessCalendar calendar1 = BusinessCalendar.newBuilder().csv(path, Duration.of(100, ChronoUnit.MILLIS)).build();
        assertCal(expected1, calendar1);
        Thread.sleep(1000);
        write(path, "");

        final BusinessCalendar expected2 = BusinessCalendar.newBuilder().build();

        Thread.sleep(3000);
        assertCal(expected2, calendar1);
    }


    @Test
    void csv() throws IOException {
        final BusinessCalendar expectedCalendar = BusinessCalendar.newBuilder()
                .on(2, DayOfWeek.SUNDAY).hours("0-24")
                .on(DayOfWeek.SUNDAY).hours("1-17,18-19")
                .on(DayOfWeek.MONDAY).hours("2-17")
                .on(DayOfWeek.TUESDAY).hours("3-17")
                .on(DayOfWeek.WEDNESDAY).hours("4-17")
                .on(DayOfWeek.THURSDAY).hours("5-17")
                .on(DayOfWeek.FRIDAY).hours("6-17")
                .on(DayOfWeek.SATURDAY).hours("7-17")
                .hours("9-18")
                .on(2021, 2, 1).holiday("just holiday")
                .on(2021, 12, 24).holiday("yet another holiday")
                .build();

        {
            final Path path = write(
                    "# abbreviation\n" +
                            "hours,2,sun,0-24\n" +
                            "hours,sun,1-17,18-19\n" +
                            "hours,mon,2-17\n" +
                            "hours,tue,3-17\n" +
                            "hours,wed,4-17\n" +
                            "hours,thu,5-17\n" +
                            "hours,fri,6-17\n" +
                            "hours,sat,7-17\n" +
                            "hours,sun,8-17\n" +
                            "ymdFormat,yyyy/M/d\n" +
                            "holiday,2021/12/24,yet another holiday\n" +
                            "ymdFormat,M/d/yyyy\n" +
                            "holiday,2/1/2021,just holiday\n"
            );
            final BusinessCalendar calendar1 = BusinessCalendar.newBuilder().csv(path).build();
            assertCal(expectedCalendar, calendar1);
        }
        {
            final Path path = write(
                    "hours,2,sunday,0-24\n" +
                            "hours,sunday,1-17,18-19\n" +
                            "hours,monday,2-17\n" +
                            "hours,tuesday,3-17\n" +
                            "hours,wednesday,4-17\n" +
                            "hours,thursday,5-17\n" +
                            "hours,friday,6-17\n" +
                            "hours,saturday,7-17\n" +
                            "hours,sunday,8-17\n" +
                            "ymdFormat,yyyy/M/d\n" +
                            "holiday,2021/12/24,yet another holiday\n" +
                            "holiday,2021/2/1,just holiday\n"
            );
            final BusinessCalendar calendar = BusinessCalendar.newBuilder().csv(path).build();
            assertCal(expectedCalendar, calendar);
        }

        {
            final BusinessCalendar expected2 = BusinessCalendar.newBuilder()
                    .hours("1-3")
                    .on(2, DayOfWeek.MONDAY).holiday("every 2nd monday is a holiday")
                    .build();

            final Path path = write(
                    "hours,1-3\n" +
                            "holiday,2,mon,every 2nd monday is a holiday\n"
            );
            final BusinessCalendar calendar = BusinessCalendar.newBuilder()
                    .csv(path)
                    .build();
            assertCal(expected2, calendar);
        }
        {
            final BusinessCalendar expected2 = BusinessCalendar.newBuilder()
                    .on(5, 22).holiday("May 22nd is a holiday")
                    .build();

            final Path path = write(
                    "holiday,5/22,May 22nd is a holiday\n"
            );
            final BusinessCalendar calendar = BusinessCalendar.newBuilder()
                    .csv(path)
                    .build();
            assertCal(expected2, calendar);
        }
    }

    @Test
    void url() throws IOException {
        final BusinessCalendar expectedCalendar = BusinessCalendar.newBuilder()
                .on(2, DayOfWeek.SUNDAY).hours("0-24")
                .on(DayOfWeek.SUNDAY).hours("1-17,18-19")
                .on(DayOfWeek.MONDAY).hours("2-17")
                .on(DayOfWeek.TUESDAY).hours("3-17")
                .on(DayOfWeek.WEDNESDAY).hours("4-17")
                .on(DayOfWeek.THURSDAY).hours("5-17")
                .on(DayOfWeek.FRIDAY).hours("6-17")
                .on(DayOfWeek.SATURDAY).hours("7-17")
                .hours("9-18")
                .on(2021, 2, 1).holiday("just holiday")
                .on(2021, 12, 24).holiday("yet another holiday")
                .build();

        {
            final Path path = write(
                    "# abbreviation\n" +
                            "hours,2,sun,0-24\n" +
                            "hours,sun,1-17,18-19\n" +
                            "hours,mon,2-17\n" +
                            "hours,tue,3-17\n" +
                            "hours,wed,4-17\n" +
                            "hours,thu,5-17\n" +
                            "hours,fri,6-17\n" +
                            "hours,sat,7-17\n" +
                            "hours,sun,8-17\n" +
                            "ymdFormat,yyyy/M/d\n" +
                            "holiday,2021/12/24,yet another holiday\n" +
                            "ymdFormat,M/d/yyyy\n" +
                            "holiday,2/1/2021,just holiday\n"
            );
            final URI uri = path.toUri();
            System.out.println(uri.toURL());
            final BusinessCalendar calendar1 = BusinessCalendar.newBuilder().csv(uri.toURL()).build();
            assertCal(expectedCalendar, calendar1);
        }


    }

    void assertCal(BusinessCalendar expected, BusinessCalendar testTarget) {
        LocalDate from = LocalDate.of(2021, 1, 1);
        LocalDate to = LocalDate.of(2021, 12, 31);
        final List<Holiday> expectedHolidays = expected.getHolidaysBetween(from, to);
        final List<Holiday> expectedHolidaysBetween = testTarget.getHolidaysBetween(from, to);
        assertEquals(expectedHolidays, expectedHolidaysBetween);
        while (from.isBefore(to)) {
            final List<BusinessHourSlot> expectedSlots = expected.
                    getBusinessHourSlots(from);
            final List<BusinessHourSlot> targetSlots = testTarget.
                    getBusinessHourSlots(from);
            assertEquals(expectedSlots, targetSlots);
            from = from.plus(1, ChronoUnit.DAYS);
        }

    }

    static Path write(String content) throws IOException {
        final Path path = File.createTempFile("test", "test").toPath();
        return write(path, content);
    }

    static Path write(Path path, String content) {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8))) {
            pw.print(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }


}
