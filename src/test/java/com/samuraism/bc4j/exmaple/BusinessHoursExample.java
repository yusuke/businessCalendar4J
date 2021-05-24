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
package com.samuraism.bc4j.exmaple;

import com.samuraism.bc4j.BusinessCalendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BusinessHoursExample {
    public static void main(String[] args) {
        BusinessCalendar calendar = BusinessCalendar.newBuilder()
                // opens 10am to 12pm, 1pm to 3pm on New Year's Eve
                .on(12, 31).hours("10 - 12, 13-15")
                // Saturday and Sunday: 10am to 12pm, 1pm to 4:30pm
                .on(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).hours("10AM-11:30 a.m., 12 noon to 4:30pm")
                // from Monday to Friday: 9am to 6pm
                .hours("9-18")
                .build();
        // prints true
        System.out.println("May 20, 2021(Thu) 9:30 on business? :" +
                calendar.isBusinessHour(LocalDateTime.of(2021, 5, 20, 9, 30)));
        // prints false
        System.out.println("May 22, 2021(Sat) 9:30 on business? :" +
                calendar.isBusinessHour(LocalDateTime.of(2021, 5, 22, 9, 30)));
        // prints [BusinessHourSlot{from=2021-12-31T10:00, to=2021-12-31T14:00}]
        System.out.println("Business hours of Dec 31, 2021(Fri) :" +
                calendar.getBusinessHourSlots(LocalDate.of(2021, 12, 31)));
    }
}
