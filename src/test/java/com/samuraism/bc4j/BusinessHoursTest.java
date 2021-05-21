package com.samuraism.bc4j;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BusinessHoursTest {
    @Test
    void businessHour24h() {
        final BusinessCalendar build = BusinessCalendar.newBuilder().build();
        // by default, opens 24 hours
        assertTrue(build.isBusinessHour());
        assertTrue(build.isBusinessHour(LocalDateTime.now()));
    }

    @Test
    void customBusinessHour() {
        // opens 9, closes 18
        final BusinessCalendar build = BusinessCalendar.newBuilder()
                // opens 10am to 2pm on Dec 31
                .on(date -> date.getMonthValue() == 12 && date.getDayOfMonth() == 31).hours("10-14")
                // everyday from 9pm to 6pm
                .hours("9-18")
                .build();

        final List<BusinessHourSlot> businessHourSlots1130 = build.getBusinessHourSlots(LocalDate.of(2021, 11, 30));
        assertEquals(1, businessHourSlots1130.size());
        assertEquals(LocalDateTime.of(2021, 11, 30, 9, 0), businessHourSlots1130.get(0).from);
        assertEquals(LocalDateTime.of(2021, 11, 30, 18, 0), businessHourSlots1130.get(0).to);

        final List<BusinessHourSlot> businessHourSlots1231 = build.getBusinessHourSlots(LocalDate.of(2021, 12, 31));
        assertEquals(1, businessHourSlots1231.size());
        assertEquals(LocalDateTime.of(2021, 12, 31, 10, 0), businessHourSlots1231.get(0).from);
        assertEquals(LocalDateTime.of(2021, 12, 31, 14, 0), businessHourSlots1231.get(0).to);

    }

    @Test
    void humanFriendlySlotExpression() {
        final LocalDate now = LocalDate.now();
        final List<BusinessHourSlot> slots = BusinessCalendar.newBuilder().hours("0-8,9-12,13:30-17,19:31-24")
                .build().getBusinessHourSlots(now);

        String[] equivalents = {
                "0-8,9-12pm,1:30pm-5pm,7:31pm-12am",
                "12 a.m. -8,9-12noon,1:30pm-5pm,7:31pm-12am",
                "12 a.m. -8,9-noon12,1:30pm-5pm,7:31pm-12am",
                "midnight12-8,9-noon12,1:30pm-5pm,7:31pm-12 midnight",
                "12 a.m. to 8,9-12,1:30pm to 5pm,7:31pm-12am",
                "午前12 から 8,9-正午,午後1:30~午後5時、午後7:31〜午前0時",
        };

        for (String equivalent : equivalents) {
            assertEquals(slots, BusinessCalendar.newBuilder().hours(equivalent).build().getBusinessHourSlots(now), equivalent);
        }

    }

    @Test
    void businessSaturdayShort() {
        // opens 9, closes 18
        final BusinessCalendar build = BusinessCalendar.newBuilder()
                .on(2021, 5, 14).holiday("just holiday")
                // Monday, Wednesday 2pm to 3pm
                .on(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY).hours("22 - 23:30,14-15")
                // Saturday 10am 12, 1:30pm to 5pm
                .on(DayOfWeek.SATURDAY).hours("10to12,13:30 to17")
                // Other than Monday, Wednesday, and Saturday: 9am to 6pm
                .hours("9-18")
                .build();

        // On Monday and wednesday, opens from 2pm to 3pm, 10pm to 11:30pm
        // Monday
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 10, 13, 45, 0)));
        // Tuesday
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 11, 13, 45, 0)));

        final List<BusinessHourSlot> businessHourSlotsMonday = build.getBusinessHourSlots(LocalDate.of(2021, 5, 10));
        assertEquals(2, businessHourSlotsMonday.size());
        assertEquals(LocalDateTime.of(2021, 5, 10, 14, 0), businessHourSlotsMonday.get(0).from);
        assertEquals(LocalDateTime.of(2021, 5, 10, 15, 0), businessHourSlotsMonday.get(0).to);
        assertEquals(LocalDateTime.of(2021, 5, 10, 22, 0), businessHourSlotsMonday.get(1).from);
        assertEquals(LocalDateTime.of(2021, 5, 10, 23, 30), businessHourSlotsMonday.get(1).to);

        final List<BusinessHourSlot> businessHourSlotsTuesday = build.getBusinessHourSlots(LocalDate.of(2021, 5, 11));
        assertEquals(1, businessHourSlotsTuesday.size());
        assertEquals(LocalDateTime.of(2021, 5, 11, 9, 0), businessHourSlotsTuesday.get(0).from);
        assertEquals(LocalDateTime.of(2021, 5, 11, 18, 0), businessHourSlotsTuesday.get(0).to);


        final List<BusinessHourSlot> businessHourSlotsWednesday = build.getBusinessHourSlots(LocalDate.of(2021, 5, 12));
        assertEquals(2, businessHourSlotsWednesday.size());
        assertEquals(LocalDateTime.of(2021, 5, 12, 14, 0), businessHourSlotsWednesday.get(0).from);
        assertEquals(LocalDateTime.of(2021, 5, 12, 15, 0), businessHourSlotsWednesday.get(0).to);
        assertEquals(LocalDateTime.of(2021, 5, 12, 22, 0), businessHourSlotsWednesday.get(1).from);
        assertEquals(LocalDateTime.of(2021, 5, 12, 23, 30), businessHourSlotsWednesday.get(1).to);

        // Wednesday
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 12, 13, 45, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 13, 13, 45, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 13, 45, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 13, 45, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 16, 13, 45, 0)));

        // Monday
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 10, 22, 45, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 11, 22, 45, 0)));
        // Wednesday
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 12, 22, 45, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 13, 22, 45, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 22, 45, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 22, 45, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 16, 22, 45, 0)));


        // Friday (holiday)
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 8, 59, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 8, 59, 59)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 9, 0, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 9, 0, 1)));

        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 12, 0, 0)));

        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 17, 59, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 17, 59, 59)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 18, 0, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 18, 0, 1)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 14, 18, 1, 0)));

        // Saturday
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 9, 59, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 9, 59, 59)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 10, 0, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 10, 0, 1)));

        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 11, 59, 59)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 12, 0, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 12, 0, 1)));

        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 13, 29, 59)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 13, 30, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 13, 30, 1)));

        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 16, 59, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 16, 59, 59)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 17, 0, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 17, 0, 1)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 15, 17, 1, 0)));
    }

    @Test
    void businessHourFrom9to18() {
        // opens 9, closes 18
        final BusinessCalendar build = BusinessCalendar.newBuilder()
                .hours("9-18").build();
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
    void until24() {
        // opens 13, closes 24
        final BusinessCalendar build = BusinessCalendar.newBuilder()
                .holiday(BusinessCalendar.CLOSED_ON_SATURDAYS_AND_SUNDAYS)
                .hours("13-24").build();
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 21, 12, 59)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 21, 13, 0)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 21, 13, 1)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 21, 23, 59)));
        assertTrue(build.isBusinessHour(LocalDateTime.of(2021, 5, 21, 23, 59)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 22, 0, 0)));
        assertFalse(build.isBusinessHour(LocalDateTime.of(2021, 5, 22, 0, 1)));
    }

    @Test
    void businessHourFrom10to12and13to17() {
        // opens 9, closes 18
        final BusinessCalendar build = BusinessCalendar.newBuilder()
                .hours("10-12,13-17").build();
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
        assertThrows(IllegalArgumentException.class, () -> BusinessCalendar.newBuilder()
                .hours("10-10").build());
        assertThrows(IllegalArgumentException.class, () -> BusinessCalendar.newBuilder()
                .hours("10-9").build());
    }

    @Test
    void hourShouldBe0to24() {
        assertDoesNotThrow(() -> {
            BusinessCalendar.newBuilder()
                    .hours("0-23:59").build();
        });
        assertDoesNotThrow(() -> {
            BusinessCalendar.newBuilder()
                    .hours("0-24:00").build();
        });
        assertThrows(IllegalArgumentException.class, () -> BusinessCalendar.newBuilder()
                .hours("-1-9").build());
        assertThrows(IllegalArgumentException.class, () -> BusinessCalendar.newBuilder()
                .hours("10-25").build());
    }

    @Test
    void nextBusinessHour() {
        {
            final BusinessCalendar build = BusinessCalendar.newBuilder().build();
            final LocalDateTime now = LocalDateTime.now();
            assertEquals(LocalDateTime.of(now.toLocalDate().plus(1, ChronoUnit.DAYS), LocalTime.of(0, 0)), build.nextBusinessHourEnd(now));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final BusinessCalendar build = BusinessCalendar.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY).build();
            final LocalDateTime atIndependenceDay = LocalDateTime.of(2021, 7, 5, 12, 0);
            assertEquals(LocalDateTime.of(2021, 7, 7, 0, 0), build.nextBusinessHourEnd(atIndependenceDay));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final BusinessCalendar build = BusinessCalendar.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY)
                    .hours("9-12,13-18").build();

            assertEquals(LocalDateTime.of(2021, 7, 6, 12, 0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021, 7, 6, 9, 30)));

            assertEquals(LocalDateTime.of(2021, 7, 6, 18, 0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021, 7, 6, 12, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 6, 18, 0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021, 7, 6, 15, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 7, 12, 0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021, 7, 6, 18, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 7, 12, 0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021, 7, 6, 19, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 7, 12, 0),
                    build.nextBusinessHourEnd(LocalDateTime.of(2021, 7, 7, 1, 30)));
        }
    }

    @Test
    void lastBusinessHourEnd() {
        {
            final BusinessCalendar build = BusinessCalendar.newBuilder().build();
            final LocalDateTime now = LocalDateTime.now();
            assertEquals(LocalDateTime.of(now.toLocalDate(), LocalTime.of(0, 0)), build.lastBusinessHourEnd(now));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final BusinessCalendar build = BusinessCalendar.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY).build();
            final LocalDateTime atIndependenceDay = LocalDateTime.of(2021, 7, 5, 12, 0);
            assertEquals(LocalDateTime.of(2021, 7, 4, 0, 0), build.lastBusinessHourEnd(atIndependenceDay));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final BusinessCalendar build = BusinessCalendar.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY)
                    .hours("9-12:5,13-18:45").build();

            assertEquals(LocalDateTime.of(2021, 7, 3, 18, 45),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021, 7, 6, 9, 30)));

            assertEquals(LocalDateTime.of(2021, 7, 6, 12, 5),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021, 7, 6, 12, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 6, 12, 5),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021, 7, 6, 15, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 6, 18, 45),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021, 7, 6, 18, 45)));
            assertEquals(LocalDateTime.of(2021, 7, 6, 18, 45),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021, 7, 6, 19, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 6, 18, 45),
                    build.lastBusinessHourEnd(LocalDateTime.of(2021, 7, 7, 1, 30)));
        }
    }

    @Test
    void lastBusinessHourStart() {
        {
            final BusinessCalendar build = BusinessCalendar.newBuilder().build();
            final LocalDateTime now = LocalDateTime.now();
            assertEquals(LocalDateTime.of(now.toLocalDate(), LocalTime.of(0, 0)), build.lastBusinessHourStart(now));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final BusinessCalendar build = BusinessCalendar.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY).build();
            final LocalDateTime atIndependenceDay = LocalDateTime.of(2021, 7, 4, 12, 0);
            assertEquals(LocalDateTime.of(2021, 7, 3, 0, 0), build.lastBusinessHourStart(atIndependenceDay));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final BusinessCalendar build = BusinessCalendar.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY)
                    .hours("9-12,13-18").build();

            assertEquals(LocalDateTime.of(2021, 7, 6, 9, 0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021, 7, 6, 9, 30)));

            assertEquals(LocalDateTime.of(2021, 7, 6, 9, 0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021, 7, 6, 12, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 6, 13, 0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021, 7, 6, 15, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 6, 13, 0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021, 7, 6, 18, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 6, 13, 0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021, 7, 6, 19, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 6, 13, 0),
                    build.lastBusinessHourStart(LocalDateTime.of(2021, 7, 7, 1, 30)));
        }
    }

    @Test
    void nextBusinessHourStart() {
        {
            // opens 24 hours
            final BusinessCalendar build = BusinessCalendar.newBuilder().build();
            final LocalDateTime now = LocalDateTime.now();
            assertEquals(LocalDateTime.of(now.toLocalDate().plus(1, ChronoUnit.DAYS), LocalTime.of(0, 0)), build.nextBusinessHourStart(now));

        }

        {
            // 2021/7/4 and 2021/7/5 are holiday
            final BusinessCalendar build = BusinessCalendar.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY).build();
            final LocalDateTime atIndependenceDay = LocalDateTime.of(2021, 7, 5, 12, 0);
            assertEquals(LocalDateTime.of(2021, 7, 6, 0, 0), build.nextBusinessHourStart(atIndependenceDay));
        }
        {
            // 2021/7/4 and 2021/7/5 are holiday
            final BusinessCalendar build = BusinessCalendar.newBuilder().holiday(UnitedStates.INDEPENDENCE_DAY)
                    .hours("9:5-12,13:30-18").build();

            assertEquals(LocalDateTime.of(2021, 7, 6, 13, 30),
                    build.nextBusinessHourStart(LocalDateTime.of(2021, 7, 6, 9, 30)));

            assertEquals(LocalDateTime.of(2021, 7, 6, 13, 30),
                    build.nextBusinessHourStart(LocalDateTime.of(2021, 7, 6, 12, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 7, 9, 5),
                    build.nextBusinessHourStart(LocalDateTime.of(2021, 7, 6, 15, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 7, 9, 5),
                    build.nextBusinessHourStart(LocalDateTime.of(2021, 7, 6, 18, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 7, 9, 5),
                    build.nextBusinessHourStart(LocalDateTime.of(2021, 7, 6, 19, 30)));
            assertEquals(LocalDateTime.of(2021, 7, 7, 9, 5),
                    build.nextBusinessHourStart(LocalDateTime.of(2021, 7, 7, 1, 30)));
        }
    }
}