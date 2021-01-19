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
package com.samuraism.holidays.en;

import com.samuraism.holidays.祝休日;

import java.time.LocalDate;
import java.util.Objects;

public final class Holiday implements Comparable<Holiday> {
    public final LocalDate date;
    public final String name;

    Holiday(祝休日 holiday){
        this.date = holiday.日付;
        this.name = holiday.名称;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holiday Holiday = (Holiday) o;
        return date.equals(Holiday.date) && name.equals(Holiday.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, name);
    }

    @Override
    public String toString() {
        return "祝休日{" +
                "date=" + date +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(Holiday o) {
        return this.date.compareTo(o.date);
    }
}
