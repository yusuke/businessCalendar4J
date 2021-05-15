# [&#35;businessCalendar4J](https://twitter.com/search?q=%23businessCalendar4J&src=typed_query&f=live)
This library provides information about businessCalendar and observances in the United States and Japan.
It is also possible to get information like "Name of the holiday on a specified date", "First business day after a specified date", and "List of businessCalendar in a specified period.".
With [HolidayConfiguration](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/HolidayConfiguration.java), you can specify non-business dates flexibly with lambda expressions, or specify fixed non-business dates at configuration time.

## Hashtag
[&#35;businessCalendar4J](https://twitter.com/intent/tweet?text=https://github.com/yusuke/businessCalendar4J/+%23businessCalendar4J)

[![@businessCal4J](https://img.shields.io/twitter/url/https/twitter.com/BusinessCal4J.svg?style=social&label=Follow%20%40BusinessCal4J)](https://twitter.com/businessCal4J)
## Requirements
Java 8 or later

## Configuration
Add a dependency declaration to pom.xml, or build.gradle as follows:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.samuraism/businessCalendar4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.samuraism/businessCalendar4j)

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
See [JapaneseHolidaysExample](https://github.com/yusuke/businessCalendar4J/blob/main/src/test/java/com/samuraism/bc4j/exmaple/JapaneseHolidaysExample.java) for Japanese businessCalendar, [UnitedStatesHolidaysExample](https://github.com/yusuke/businessCalendar4J/blob/main/src/test/java/com/samuraism/bc4j/exmaple/UnitedStatesHolidaysExample.java) for the United States businessCalendar.

```java
import com.samuraism.bc4j.BusinessCalendar;
import com.samuraism.bc4j.UnitedStates;

import java.time.LocalDate;

public class UnitedStatesHolidaysExample {
    public static void main(String[] args) {
        BusinessCalendar calendar = BusinessCalendar.newBuilder()
                .holiday(UnitedStates.PUBLIC_HOLIDAYS)
                .build();

        // prints true, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a holiday?: "
                + calendar.isHoliday(LocalDate.of(2021, 1, 1)));
        // prints false, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a business day?: "
                + calendar.isBusinessDay(LocalDate.of(2021, 1, 1)));

        // get Martin Luther King Jr. Day
        System.out.println("What is Jan, 18 2021?: "
                + calendar.getHoliday(LocalDate.of(2021, 1, 18)));

        System.out.println("List of holidays in 2021: ");
        calendar.getHolidaysBetween(LocalDate.of(2021, 1, 1)
                , LocalDate.of(2021, 12, 31))
                .forEach(e -> System.out.println(e.date + ": " + e.name));

        // sets a fixed custom Holiday
        BusinessCalendar customCalendar = BusinessCalendar.newBuilder()
                .holiday(UnitedStates.NEW_YEARS_DAY,
                        UnitedStates.MARTIN_LUTHER_KING_JR_DAY,
                        UnitedStates.MEMORIAL_DAY,
                        UnitedStates.INDEPENDENCE_DAY,
                        UnitedStates.LABOR_DAY,
                        UnitedStates.VETERANS_DAY,
                        UnitedStates.THANKS_GIVING_DAY,
                        UnitedStates.CHRISTMAS_DAY,
                        BusinessCalendar.CLOSED_ON_SATURDAYS_AND_SUNDAYS,
                        // Specify logic based custom holidays. returns a string if the day is a holiday
                        e -> e.getMonthValue() == 5 && e.getDayOfMonth() == 19 ? "James Gosling's birthday" : null)
                .holiday(LocalDate.of(1995, 5, 23), "Java public debut")
                .build();

        // Gets the last business day of Jan, 2021 → the answer is Jan 29 since Jan 30, 31 are weekend
        System.out.println("Last business day of Jan 2021: "
                + customCalendar.lastBusinessDay(LocalDate.of(2021, 1, 31)));
        // Gets the first business day on and after July 4, 2021
        // The answer is July 6, 2021 because July 4 and 5 are the Independence day and it's substitute
        System.out.println("First business day on or after July 4, 2021: "
                + customCalendar.firstBusinessDay(LocalDate.of(2021, 7, 4)));
        // First holiday on and after Dec 20, 2021 →  Dec 24 (Christmas Day)
        System.out.println(customCalendar.firstHoliday(LocalDate.of(2021, 12, 20)));
        // Last holiday by Nov 12, 2021 →  Nov 11 (Veterans Day)
        System.out.println(customCalendar.lastHoliday(LocalDate.of(2021, 11, 12)));
    }
}
```

# License
Apache License Version 2.0

![Java CI with Gradle](https://github.com/yusuke/businessCalendar4j/workflows/Java%20CI%20with%20Gradle/badge.svg)