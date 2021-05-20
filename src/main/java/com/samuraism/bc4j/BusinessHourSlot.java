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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public final class BusinessHourSlot {
    public final LocalDateTime from, to;

    BusinessHourSlot(@NotNull LocalDate baseDate, LocalTime from, @NotNull LocalTime to) {
        this.from = LocalDateTime.of(baseDate, from);
        this.to = to.getHour() == 0 && to.getMinute() == 0 ? LocalDateTime.of(baseDate.plus(1, ChronoUnit.DAYS), to)
                : LocalDateTime.of(baseDate, to);
    }

    boolean isBusinessHour(@NotNull LocalDateTime time) {
        return time.isEqual(from) || (from.isBefore(time) && to.isAfter(time));
    }
}

