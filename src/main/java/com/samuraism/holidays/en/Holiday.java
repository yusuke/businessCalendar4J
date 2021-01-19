package com.samuraism.holidays.en;

import com.samuraism.holidays.祝休日;

import java.time.LocalDate;
import java.util.Objects;

public final class Holiday implements Comparable<Holiday> {
    public final LocalDate date;
    public final String name;

    public Holiday(祝休日 holiday){
        this.date = holiday.日付;
        this.name = holiday.名称;
    }

    public Holiday(LocalDate date, String name) {
        this.date = date;
        this.name = name;
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
