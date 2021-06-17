# Business Calendar library for Java
[&#35;businessCalendar4J](https://twitter.com/search?q=%23businessCalendar4J&src=typed_query&f=live) is a 100% pure Java business calendar library with no extra dependency. You can configure holidays and business hours flexibly, and use predefined public holidays in United States and Japan. Need support for other countries' public holidays? Please contribute!

It is also possible to get information like "Name of the holiday on a specified date", "First business day after a specified date", and "List of holidays in a specified period.".

With [BusinessCalenderBuilder](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/BusinessCalendarBuilder.java), you can specify non-business dates flexibly with lambda expressions, or specify fixed non-business dates at configuration time.

[![@businessCal4J](https://img.shields.io/twitter/url/https/twitter.com/BusinessCal4J.svg?style=social&label=Follow%20%40BusinessCal4J)](https://twitter.com/businessCal4J)

## Requirements
Java 8 or later

## Dependency declaration
Add a dependency declaration to pom.xml, or build.gradle as follows:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/one.cafebabe/businessCalendar4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/one.cafebabe/businessCalendar4j)

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>one.cafebabe</groupId>
        <artifactId>businessCalendar4j</artifactId>
        <version>1.21</version>
    </dependency>
</dependencies>
```
### Gradle
```text
dependencies {
    compile 'one.cafebabe:businessCalendar4j:1.21'
}
```

## Getting started

### initialization

- Initialization ceremony: 
```java
BusinessCalendar calendar = BusinessCalendar.newBuilder()
    // where you configure holidays and business hours
    .build();
```

Conditions for holidays and business hours are evaluated in the method chain order.

### Configuration

#### Specify holidays and business hours

- Set fixed holidays by year-month-day, or month-day.

```java
BusinessCalendar calendar = BusinessCalendar.newBuilder()
    // fixed one-time holiday
    .on(1995, 5, 23).holiday("Java public debut")
    // occurs every year
    .on(5, 19).holiday("James Gosling's birthday")
    .build();
```

- Specify holidays by day of weeks.

```java
BusinessCalendar calendar = BusinessCalendar.newBuilder()
    // fixed holiday by day of weeks.
    .on(DayOfWeek.SUNDAY, DayOfWeek.Wednesday)
        .holiday("closed on Sunday an Wednesday")
    // occurs every 2nd Monday
    .on(2, DayOfWeek.Monday).holiday("closed on every 2nd Monday")
    .build();
```

- Specify business hours by day of weeks, and month-day.

```java
BusinessCalendar calendar = BusinessCalendar.newBuilder()
    // opens 10am to 12pm, 1pm to 3pm on New Year's Eve
    .on(12, 31).hours("10 - 12, 13-15")
    // Saturday and Sunday: 10am to 11:30pm, 1pm to 4:30pm
    .on(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        .hours("10AM-11:30 a.m., 1pm to 4:30pm")
    // from Monday to Friday: 9am to 6pm
    .hours("9-18")
    .build();
```

Dates that are not specified business hours are treated as 24 hours open business days.

Below are valid business hour expressions for "Opens from midnight to 8:30am, 9am to noon, 1:30pm to 5pm, 7:31pm to midnight".

| business hours |
| ---- |
| "0-8:30,9-12,13:30-17,19:31-24" |
| "0-8:30,9-12,13:30-17,19:31-0" |
| "0-8:30,9-12pm,1:30pm-5pm,7:31pm-12am" |
| "12 a.m.-8:30,9-12noon, 1:30pm-5pm, 7:31pm-12am" |
| "12 a.m. - 8:30, 9 - noon,1:30pm-5pm,7:31pm-12am" |
| "midnight12 - 8:30, 9- noon 12, 1:30pm-5pm,7:31pm-12 midnight" |
| "12 a.m. to 8:30, 9-12,1:30pm to 5pm, 7:31pm-12am" |
| "12 a.m. to 8:30 & 9-12,1:30pm to 5pm&7:31pm-12am" |
| "午前12時 から 午前8時半, 9-正午,午後1時半~午後5時、午後7:31〜午前0時" |

- Configure holidays and business hours with CSV formatted files.

From file system:

```java
BusinessCalendar calendar = BusinessCalendar.newBuilder()
    .csv(Paths.get("holidays-business-hours.csv"))
    .build();
```

From URL:

```java
BusinessCalendar calendar = BusinessCalendar.newBuilder()
    .csv(new URL("https://somewhere.example.com/holidays-business-hours.csv"))
    .build();
```

- Configure holidays and business hours with a CSV formatted text file, reload every hour

```java
BusinessCalendar calendar = BusinessCalendar.newBuilder()
    .csv(Paths.get("holidays-business-hours.csv"), Duration.of(1, ChronoUnit.HOURS))
    .build();
```

- Configure holidays and business hours with a CSV formatted text file, reload on demand.

```java
CsvConfiguration conf = CsvConfiguration.getInstance(Paths.get("holidayconf.csv"));
BusinessCalendar calendar = BusinessCalendar.newBuilder()
    .csv(conf);
    .build();
.
.
.
// reload at anytime you need.
conf.reload();
```

Below is a valid configuration file format example.

```text
# comment
# opens 24 hours on every 2nd Sunday
hours,2,sun,0-24
hours,sun,1-17,18-19
hours,mon,2-17
hours,tue,3-17
hours,wed,4-17
hours,thu,5-17
hours,fri,6-17
hours,sat,7-17
hours,sun,8-17
holiday,5/1,May 1st
holiday,2021/12/2,Dec 2, 2021
ymdFormat,M/d/yyyy
holiday,2/1/2021,just another holiday
```

#### Predefined holidays
Predefined public holidays are available in [UnitedStates](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/UnitedStates.java) and [Japan](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/Japan.java).

- Apply Martin Luther King Jr. Day and Independence Day 

```java
BusinessCalendar usCal = BusinessCalendar.newBuilder()
    .holiday(BusinessCalendar.UNITED_STATES.MARTIN_LUTHER_KING_JR_DAY)
    .holiday(BusinessCalendar.UNITED_STATES.INDEPENDENCE_DAY)
    .build();
```

Available holidays are: NEW_YEARS_DAY, MARTIN_LUTHER_KING_JR_DAY,
MEMORIAL_DAY, INDEPENDENCE_DAY, LABOR_DAY, VETERANS_DAY, THANKS_GIVING_DAY, CHRISTMAS_DAY

-  Apply all holidays in U.S.

```java
BusinessCalendar usCal = BusinessCalendar.newBuilder()
    .holiday(BusinessCalendar.UNITED_STATES.PUBLIC_HOLIDAYS)
    .build();
```

- Apply Japanese public holidays
```java
BusinessCalendar japanCal = BusinessCalendar.newBuilder()
    .holiday(BusinessCalendar.JAPAN.PUBLIC_HOLIDAYS)
    .build();
```

- Closed on Saturday and Sunday
```java
BusinessCalendar weekDays = BusinessCalendar.newBuilder()
    .holiday(BusinessCalendar.CLOSED_ON_SATURDAYS_AND_SUNDAYS)
    .build();
```

### Test holidays

- Test today is a holiday or business day

```java
BusinessCalendar cal = ...
System.out.println("Is it holiday? " + cal.isHoliday());
System.out.println("Is it business day? " + cal.isBusinessDay());
```

- Test whether specified date is a holiday or business day

```java
BusinessCalendar cal = ...
LocalDate may24 = LocalDate.of(2021, 5, 24);
System.out.println("Is it holiday? " + cal.isHoliday(may24));
System.out.println("Is it business day? " + cal.isBusinessDay(may24));
```

- Get next, or previous holiday or business day

```java
BusinessCalendar cal = ...
System.out.println("What date is the next holiday? "
    + cal.firstHoliday(LocalDate.of(2021, 5, 24)));
System.out.println("What date was the previous holiday? "
    + cal.lastHoliday(LocalDate.of(2021, 5, 24)));
LocalDate independenceDay = LocalDate.of(2021, 7, 4);
System.out.println("What date is the next business day? "
    + cal.firstBusinessDay(independenceDay));
System.out.println("What date was the previous business day? "
    + cal.lastBusinessDay(independenceDay));
```

Note that the date is inclusive. In other words, lastHoliday() / firstHoliday() returns the same date if the specified date is a holiday.

### Test business hours

- Test if it's during business hours

```java
BusinessCalendar cal = ...
System.out.println("Is it open? " + cal.isBusinessHour());
```

- Test whether specified time is during business hours

```java
BusinessCalendar cal = ...
LocalDateTime may241023 = LocalDateTime.of(2021, 5, 24, 10, 23);
System.out.println("Is it open? " + cal.isBusinessHour(may241023));
```

- Get next, or previous business hour start / end

```java
BusinessCalendar cal = ...
LocalDateTime may241023 = LocalDateTime.of(2021, 5, 24, 10, 23);
System.out.println("When will this business hour slot close? " + cal.nextBusinessHourEnd(may241023));
System.out.println("When what the last business hour slot started? " + cal.lastBusinessHourStart(may241023));
```

- Get [business hour slots](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/BusinessHourSlot.java)  for a specified date

```java
BusinessCalendar cal = ...
LocalDateTime may24 = LocalDateTime.of(2021, 5, 24);
// returns an empty list if the specified date is a holiday
List<BusinessHourSlot> slots = cal.getBusinessHourSlots(may24);
System.out.println("Number of business hour slots on May 24, 2021: " + slots.size());
System.out.println("On May 24, 2021, the buness starts from: " + slots.get(0).from;
```

- Get [holidays](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/Holiday.java) and business days in a specified period

```java
BusinessCalendar cal = ...
List<Holiday> holidays = cal.getHolidaysBetween(LocalDateTime.of(2021, 1, 1),
    LocalDateTime.of(2021, 12, 21));
System.out.println("Holidays in 2021: " + holidays);
List<LocalDate> businessDays = cal.getBusinessDaysBetween(LocalDateTime.of(2021, 1, 1),
    LocalDateTime.of(2021, 12, 21));
System.out.println("Business days in 2021: " + businessDays);
```

## Example codes
For holidays, see [JapaneseHolidaysExample](https://github.com/yusuke/businessCalendar4J/blob/main/src/test/java/com/samuraism/bc4j/exmaple/JapaneseHolidaysExample.java) for Japanese businessCalendar, [UnitedStatesHolidaysExample](https://github.com/yusuke/businessCalendar4J/blob/main/src/test/java/com/samuraism/bc4j/exmaple/UnitedStatesHolidaysExample.java) for the United States businessCalendar.

For business hours, see [BusinessHoursExample](https://github.com/yusuke/businessCalendar4J/blob/main/src/test/java/com/samuraism/bc4j/exmaple/BusinessHoursExample.java).

## License
Apache License Version 2.0

![Java CI with Gradle](https://github.com/yusuke/businessCalendar4j/workflows/Java%20CI%20with%20Gradle/badge.svg)