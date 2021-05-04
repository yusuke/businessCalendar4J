# [&#35;javaHolidays](https://twitter.com/search?q=%23javaHolidays&src=typed_query&f=live)
This library provides information about holidays and observances in the United States and Japan.
It is also possible to get information like "Name of the holiday on a specified date", "First business day after a specified date", and "List of holidays in a specified period.".
With [HolidayConfiguration](https://github.com/yusuke/holidays/blob/main/src/main/java/com/samuraism/holidays/HolidayConfiguration.java), you can specify non-business dates flexibly with lambda expressions, or specify fixed non-business dates at configuration time.

## Hashtag
[&#35;javaHolidays](https://twitter.com/intent/tweet?text=https://github.com/yusuke/holidays/+%23javaHolidays)

[![@HolidaysJava](https://img.shields.io/twitter/url/https/twitter.com/HolidaysJava.svg?style=social&label=Follow%20%40HolidaysJava)](https://twitter.com/HolidaysJava)
## Requirements
Java 8 or later

## Configuration
Add a dependency declaration to pom.xml, or build.gradle as follows:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.samuraism/holidays/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.samuraism/holidays)

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>com.samuraism</groupId>
        <artifactId>holidays</artifactId>
        <version>1.6</version>
    </dependency>
</dependencies>
```
### Gradle
```text
dependencies {
    compile 'com.samuraism:holidays:1.6'
}
```
## How to use
See [JapaneseHolidaysExample](https://github.com/yusuke/holidays/blob/main/src/test/java/com/samuraism/holidays/exmaple/JapaneseHolidaysExample.java) for Japanese holidays, [UnitedStatesHolidaysExample](https://github.com/yusuke/holidays/blob/main/src/test/java/com/samuraism/holidays/exmaple/UnitedStatesHolidaysExample.java) for the United States holidays.

```java
import com.samuraism.holidays.Holiday;
import com.samuraism.holidays.UnitedStatesHolidays;

import java.time.LocalDate;
import java.util.Optional;

import static com.samuraism.holidays.UnitedStatesHolidays.*;

public class UnitedStatesHolidaysExample {
    public static void main(String[] args) {
        UnitedStatesHolidays holidays = UnitedStatesHolidays.getInstance(conf -> conf
                .holiday(NEW_YEARS_DAY,
                        MARTIN_LUTHER_KING_JR_DAY,
                        MEMORIAL_DAY,
                        INDEPENDENCE_DAY,
                        LABOR_DAY,
                        VETERANS_DAY,
                        THANKS_GIVING_DAY,
                        CHRISTMAS_DAY));

        // prints true, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a holiday?: "
                + holidays.isHoliday(LocalDate.of(2021, 1, 1)));
        // prints false, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a business day?: "
                + holidays.isBusinessDay(LocalDate.of(2021, 1, 1)));

        // get Martin Luther King Jr. Day
        Optional<Holiday> holiday = holidays.getHoliday(LocalDate.of(2021, 1, 18));
        holiday.ifPresent(e -> System.out.println("What is Jan, 18 2021?: " + e.name));

        System.out.println("List of holidays in 2021: ");
        holidays.getHolidaysBetween️(LocalDate.of(2021, 1, 1)
                , LocalDate.of(2021, 12, 31))
                .forEach(e -> System.out.println(e.date + ": " + e.name));

        // sets a fixed custom Holiday
        UnitedStatesHolidays customHolidays = UnitedStatesHolidays.getInstance(conf -> conf
                .holiday(NEW_YEARS_DAY,
                        MARTIN_LUTHER_KING_JR_DAY,
                        MEMORIAL_DAY,
                        INDEPENDENCE_DAY,
                        LABOR_DAY,
                        VETERANS_DAY,
                        THANKS_GIVING_DAY,
                        CHRISTMAS_DAY,
                        CLOSED_ON_SATURDAYS_AND_SUNDAYS,
                        // Specify logic based custom holidays. returns a string if the day is a holiday
                        e -> e.getMonthValue() == 5 && e.getDayOfMonth() == 19 ? "James Gosling's birthday" : null)
                .holiday(LocalDate.of(1995, 5, 23), "Java public debut"));

        // Gets the last business day of Jan, 2021 → the answer is Jan 29 since Jan 30, 31 are weekend
        System.out.println("Last business day of Jan 2021: "
                + customHolidays.lastBusinessDay(LocalDate.of(2021, 1, 31)));
        // Gets the first business day on and after July 4, 2021
        // The answer is July 6, 2021 because July 4 and 5 are the Independence day and it's substitute
        System.out.println("First business day on or after July 4, 2021: "
                + customHolidays.firstBusinessDay(LocalDate.of(2021, 7, 4)));
        // First holiday on and after Dec 20, 2021 →  Dec 24 (Christmas Day)
        System.out.println(customHolidays.firstHoliday(LocalDate.of(2021, 12, 20)));
        // Last holiday by Nov 12, 2021 →  Nov 11 (Veterans Day)
        System.out.println(customHolidays.lastHoliday(LocalDate.of(2021, 11, 12)));
    }
}
```

# License
Apache License Version 2.0

![Java CI with Gradle](https://github.com/yusuke/holidays/workflows/Java%20CI%20with%20Gradle/badge.svg)