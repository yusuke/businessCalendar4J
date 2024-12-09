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
package one.cafebabe.businesscalendar4j;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a slot of business hours defined by a starting and ending time.
 * This class is immutable and thread-safe, and it also implements the Serializable interface.
 * <p>
 * The time range is represented using LocalDateTime objects for both the from and to parameters.
 * The BusinessHourSlot object can be constructed using either LocalDateTime or by providing
 * a LocalDate along with LocalTime for starting and ending times.
 * <p>
 * The end time is adjusted to be on the next day if it represents the start of the next day (00:00).
 *
 * @param from start time of the business hour slot
 * @param to end time of the business hour slot
 */
public record BusinessHourSlot(@NotNull LocalDateTime from, @NotNull LocalDateTime to)
        implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = -102401732734407839L;


    BusinessHourSlot(@NotNull LocalDate baseDate, @NotNull LocalTime from, @NotNull LocalTime to) {
        this(LocalDateTime.of(baseDate, from),
                to.getHour() == 0 && to.getMinute() == 0 ? LocalDateTime.of(baseDate.plusDays(1), to)
                : LocalDateTime.of(baseDate, to));
    }

    boolean isBusinessHour(@NotNull LocalDateTime time) {
        return time.isEqual(from) || (from.isBefore(time) && to.isAfter(time));
    }
}

