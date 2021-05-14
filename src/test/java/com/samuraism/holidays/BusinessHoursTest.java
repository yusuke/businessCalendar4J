package com.samuraism.holidays;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class BusinessHoursTest {
    @Test
    void businessHour24h() {
        final Holidays build = Holidays.newBuilder().build();
        // by default, opens 24 hours
        assertTrue(build.isBusinessHour());
        assertTrue(build.isBusinessHour(LocalDateTime.now()));
    }

    @Test
    void businessHourFrom9to18() {
        // opens 9, closes 18
        final Holidays build = Holidays.newBuilder()
                .businessHourFrom(9).to(18).build();
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 8, 59, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 8, 59, 59)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 9, 0, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 9, 0, 1)));

        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 12, 0, 0)));

        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 17, 59, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 17, 59, 59)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 18, 0, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 18, 0, 1)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 18, 1, 0)));
    }

    @Test
    void businessHourFrom10to12and13to17() {
        // opens 9, closes 18
        final Holidays build = Holidays.newBuilder()
                .businessHourFrom(10).to(12)
                .businessHourFrom(13).to(17).build();
        // business hour starts from 10
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 9, 59, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 9, 59, 59)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 10, 0, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 10, 0, 1)));

        // business hour ends at 12
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 11, 59, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 11, 59, 59)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 12, 0, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 12, 0, 1)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 12, 1, 0)));

        // business hour starts from 13
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 12, 59, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 12, 59, 59)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 13, 0, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 13, 0, 1)));


        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 15, 0, 0)));

        // business hour ends at 17
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 16, 59, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 16, 59, 59)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 17, 0, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 17, 0, 1)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 17, 1, 0)));
    }

    @Test
    void fromShouldBeforeTo() {
        assertThrows(IllegalArgumentException.class, () -> Holidays.newBuilder()
                .businessHourFrom(10).to(10).build());
        assertThrows(IllegalArgumentException.class, () -> Holidays.newBuilder()
                .businessHourFrom(10).to(9).build());
    }

    @Test
    void hourShouldBe0to24() {
        assertDoesNotThrow(() -> {
            Holidays.newBuilder()
                    .businessHourFrom(0).to(23,59).build();
        });
        assertThrows(IllegalArgumentException.class,() -> Holidays.newBuilder()
                .businessHourFrom(0).to(24).build());
        assertThrows(IllegalArgumentException.class, () -> Holidays.newBuilder()
                .businessHourFrom(-1).to(9).build());
        assertThrows(IllegalArgumentException.class, () -> Holidays.newBuilder()
                .businessHourFrom(10).to(25).build());
    }

    @Test
    void nextBusinessHour() {
        {
            final Holidays build = Holidays.newBuilder().build();
            final LocalDateTime now = LocalDateTime.now();
            assertEquals(LocalDateTime.of(now.toLocalDate().plus(1, ChronoUnit.DAYS), LocalTime.of(0, 0)), build.nextBusinessHourEnd(now));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final Holidays build = Holidays.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY).build();
            final LocalDateTime atIndependenceDay = LocalDateTime.of(2021,7,5,12,0);
            assertEquals(LocalDateTime.of(2021,7,7,0,0), build.nextBusinessHourEnd(atIndependenceDay));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final Holidays build = Holidays.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY)
                    .businessHourFrom(9).to(12)
                    .businessHourFrom(13).to(18).build();

            assertEquals(LocalDateTime.of(2021,7,6,12,0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021,7,6,9,30)));

            assertEquals(LocalDateTime.of(2021,7,6,18,0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021,7,6,12,30)));
            assertEquals(LocalDateTime.of(2021,7,6,18,0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021,7,6,15,30)));
            assertEquals(LocalDateTime.of(2021,7,7,12,0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021,7,6,18,30)));
            assertEquals(LocalDateTime.of(2021,7,7,12,0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021,7,6,19,30)));
            assertEquals(LocalDateTime.of(2021,7,7,12,0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021,7,7,1,30)));
        }
    }

    @Test
    void lastBusinessHourEnd() {
        {
            final Holidays build = Holidays.newBuilder().build();
            final LocalDateTime now = LocalDateTime.now();
            assertEquals(LocalDateTime.of(now.toLocalDate(), LocalTime.of(0, 0)), build.lastBusinessHourEnd(now));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final Holidays build = Holidays.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY).build();
            final LocalDateTime atIndependenceDay = LocalDateTime.of(2021,7,5,12,0);
            assertEquals(LocalDateTime.of(2021,7,4,0,0), build.lastBusinessHourEnd(atIndependenceDay));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final Holidays build = Holidays.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY)
                    .businessHourFrom(9).to(12,5)
                    .businessHourFrom(13).to(18,45).build();

            assertEquals(LocalDateTime.of(2021,7,3,18,45),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021,7,6,9,30)));

            assertEquals(LocalDateTime.of(2021,7,6,12,5),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021,7,6,12,30)));
            assertEquals(LocalDateTime.of(2021,7,6,12,5),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021,7,6,15,30)));
            assertEquals(LocalDateTime.of(2021,7,6,18,45),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021,7,6,18,45)));
            assertEquals(LocalDateTime.of(2021,7,6,18,45),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021,7,6,19,30)));
            assertEquals(LocalDateTime.of(2021,7,6,18,45),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021,7,7,1,30)));
        }
    }

    @Test
    void lastBusinessHourStart() {
        {
            final Holidays build = Holidays.newBuilder().build();
            final LocalDateTime now = LocalDateTime.now();
            assertEquals(LocalDateTime.of(now.toLocalDate(), LocalTime.of(0, 0)), build.lastBusinessHourStart(now));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final Holidays build = Holidays.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY).build();
            final LocalDateTime atIndependenceDay = LocalDateTime.of(2021,7,4,12,0);
            assertEquals(LocalDateTime.of(2021,7,3,0,0), build.lastBusinessHourStart(atIndependenceDay));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final Holidays build = Holidays.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY)
                    .businessHourFrom(9).to(12)
                    .businessHourFrom(13).to(18).build();

            assertEquals(LocalDateTime.of(2021,7,6,9,0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021,7,6,9,30)));

            assertEquals(LocalDateTime.of(2021,7,6,9,0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021,7,6,12,30)));
            assertEquals(LocalDateTime.of(2021,7,6,13,0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021,7,6,15,30)));
            assertEquals(LocalDateTime.of(2021,7,6,13,0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021,7,6,18,30)));
            assertEquals(LocalDateTime.of(2021,7,6,13,0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021,7,6,19,30)));
            assertEquals(LocalDateTime.of(2021,7,6,13,0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021,7,7,1,30)));
        }
    }

    @Test
    void nextBusinessHourStart() {
        {
            // opens 24 hours
            final Holidays build = Holidays.newBuilder().build();
            final LocalDateTime now = LocalDateTime.now();
            assertEquals(LocalDateTime.of(now.toLocalDate().plus(1, ChronoUnit.DAYS), LocalTime.of(0, 0)), build.nextBusinessHourStart(now));

        }

        {
            // 2021/7/4 and 2021/7/5 are holiday
            final Holidays build = Holidays.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY).build();
            final LocalDateTime atIndependenceDay = LocalDateTime.of(2021,7,5,12,0);
            assertEquals(LocalDateTime.of(2021,7,6,0,0), build.nextBusinessHourStart(atIndependenceDay));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final Holidays build = Holidays.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY)
                    .businessHourFrom(9,5).to(12)
                    .businessHourFrom(13,30).to(18).build();

            assertEquals(LocalDateTime.of(2021,7,6,13,30),
                    build.nextBusinessHourStart(LocalDateTime.of(2021,7,6,9,30)));

            assertEquals(LocalDateTime.of(2021,7,6,13,30),
                    build.nextBusinessHourStart(LocalDateTime.of(2021,7,6,12,30)));
            assertEquals(LocalDateTime.of(2021,7,7,9,5),
                    build.nextBusinessHourStart(LocalDateTime.of(2021,7,6,15,30)));
            assertEquals(LocalDateTime.of(2021,7,7,9,5),
                    build.nextBusinessHourStart(LocalDateTime.of(2021,7,6,18,30)));
            assertEquals(LocalDateTime.of(2021,7,7,9,5),
                    build.nextBusinessHourStart(LocalDateTime.of(2021,7,6,19,30)));
            assertEquals(LocalDateTime.of(2021,7,7,9,5),
                    build.nextBusinessHourStart(LocalDateTime.of(2021,7,7,1,30)));
        }
    }
}