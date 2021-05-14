# [&#35;javaHolidays](https://twitter.com/search?q=%23javaHolidays&src=typed_query&f=live)
This library provides information about businessCalendar and observances in the United States and Japan.
It is also possible to get information like "Name of the holiday on a specified date", "First business day after a specified date", and "List of businessCalendar in a specified period.".
With [HolidayConfiguration](https://github.com/yusuke/businessCalendar/blob/main/src/main/java/com/samuraism/businessCalendar/HolidayConfiguration.java), you can specify non-business dates flexibly with lambda expressions, or specify fixed non-business dates at configuration time.

## Hashtag
[&#35;javaHolidays](https://twitter.com/intent/tweet?text=https://github.com/yusuke/businessCalendar/+%23javaHolidays)

[![@HolidaysJava](https://img.shields.io/twitter/url/https/twitter.com/HolidaysJava.svg?style=social&label=Follow%20%40HolidaysJava)](https://twitter.com/HolidaysJava)
## Requirements
Java 8 or later

## Configuration
Add a dependency declaration to pom.xml, or build.gradle as follows:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.samuraism/businessCalendar/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.samuraism/businessCalendar)

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>com.samuraism</groupId>
        <artifactId>businessCalendar4j</artifactId>
        <version>1.8</version>
    </dependency>
</dependencies>
```
### Gradle
```text
dependencies {
    compile 'com.samuraism:businessCalendar4j:1.8'
}
```
## How to use
See [JapaneseHolidaysExample](https://github.com/yusuke/businessCalendar4j/blob/main/src/test/java/com/samuraism/businessCalendar/exmaple/JapaneseHolidaysExample.java) for Japanese businessCalendar, [UnitedStatesHolidaysExample](https://github.com/yusuke/businessCalendar/blob/main/src/test/java/com/samuraism/businessCalendar/exmaple/UnitedStatesHolidaysExample.java) for the United States businessCalendar.

```java
import com.samuraism.businessCalendar.Holiday;
import com.samuraism.holidays.BusinessCalendarHolidays;
import com.samuraism.businessCalendar.UnitedStates;

import java.time.LocalDate;
import java.util.Optional;

public class UnitedStatesHolidaysExample {
    public static void main(String[] args) {
        Holidays businessCalendar = Holidays.newBuilder()
                .holiday(UnitedStates.PUBLIC_HOLIDAYS)
                .build();

        // prints true, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a holiday?: "
                + businessCalendar.isHoliday(LocalDate.of(2021, 1, 1)));
        // prints false, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a business day?: "
                + businessCalendar.isBusinessDay(LocalDate.of(2021, 1, 1)));

        // get Martin Luther King Jr. Day
        Optional<Holiday> holiday = businessCalendar.getHoliday(LocalDate.of(2021, 1, 18));
        holiday.ifPresent(e -> System.out.println("What is Jan, 18 2021?: " + e.name));

        System.out.println("List of businessCalendar in 2021: ");
        businessCalendar.getHolidaysBetween️(LocalDate.of(2021, 1, 1)
                , LocalDate.of(2021, 12, 31))
                .forEach(e -> System.out.println(e.date + ": " + e.name));

        // sets a fixed custom Holiday
        Holidays customBusinessCalendar = Holidays.newBuilder()
                .holiday(UnitedStates.NEW_YEARS_DAY,
                        UnitedStates.MARTIN_LUTHER_KING_JR_DAY,
                        UnitedStates.MEMORIAL_DAY,
                        UnitedStates.INDEPENDENCE_DAY,
                        UnitedStates.LABOR_DAY,
                        UnitedStates.VETERANS_DAY,
                        UnitedStates.THANKS_GIVING_DAY,
                        UnitedStates.CHRISTMAS_DAY,
                        Holidays.CLOSED_ON_SATURDAYS_AND_SUNDAYS,
                        // Specify logic based custom businessCalendar. returns a string if the day is a holiday
                        e -> e.getMonthValue() == 5 && e.getDayOfMonth() == 19 ? "James Gosling's birthday" : null)
                .holiday(LocalDate.of(1995, 5, 23), "Java public debut")
                .build();

        // Gets the last business day of Jan, 2021 → the answer is Jan 29 since Jan 30, 31 are weekend
        System.out.println("Last business day of Jan 2021: "
                + customBusinessCalendar.lastBusinessDay(LocalDate.of(2021, 1, 31)));
        // Gets the first business day on and after July 4, 2021
        // The answer is July 6, 2021 because July 4 and 5 are the Independence day and it's substitute
        System.out.println("First business day on or after July 4, 2021: "
                + customBusinessCalendar.firstBusinessDay(LocalDate.of(2021, 7, 4)));
        // First holiday on and after Dec 20, 2021 →  Dec 24 (Christmas Day)
        System.out.println(customBusinessCalendar.firstHoliday(LocalDate.of(2021, 12, 20)));
        // Last holiday by Nov 12, 2021 →  Nov 11 (Veterans Day)
        System.out.println(customBusinessCalendar.lastHoliday(LocalDate.of(2021, 11, 12)));
    }
}
```

# License
Apache License Version 2.0

![Java CI with Gradle](https://github.com/yusuke/businessCalendar4j/workflows/Java%20CI%20with%20Gradle/badge.svg)