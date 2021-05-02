package com.samuraism.holidays;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

public class UnitedStatesHolidays extends Holidays {
    @SafeVarargs
    public UnitedStatesHolidays(Locale locale, Function<LocalDate, String>... logics) {
        super(ResourceBundle.getBundle("unitedStates/holidays", locale));
        for (Function<LocalDate, String> logic : logics) {
            addHoliday(logic);
        }
    }

    @SuppressWarnings("unused")
    @SafeVarargs
    public UnitedStatesHolidays(Function<LocalDate, String>... logics) {
        this(Locale.getDefault(), logics);
    }

    @SuppressWarnings("unused")
    public UnitedStatesHolidays() {
        this(Locale.getDefault());
    }

    /**
     * Fixed algorithm to close on Saturdays and Sundays
     *
     * @since 1.6
     */
    public static final Function<LocalDate, String> CLOSED_ON_SATURDAYS_AND_SUNDAYS = localDate -> {
        switch (localDate.getDayOfWeek()) {
            case SATURDAY:
                return "Saturday";
            case SUNDAY:
                return "Sunday";
            default:
                return null;
        }
    };

    public static final Function<LocalDate, String> NEW_YEARS_DAY = e -> substitution(e, e2 -> e2.getMonthValue() == 1 && e2.getDayOfMonth() == 1 ? "NewYearsDay" : null);
    public static final Function<LocalDate, String> MARTIN_LUTHER_KING_JR_DAY = e -> e.getMonthValue() == 1 && e.getDayOfMonth() ==
            e.with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY)).getDayOfMonth() ? "MartinLutherKingJrDay" : null;
    public static final Function<LocalDate, String> MEMORIAL_DAY = e -> e.getMonthValue() == 5 && e.getDayOfMonth() ==
            e.with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY)).getDayOfMonth() ? "MemorialDay" : null;
    public static final Function<LocalDate, String> INDEPENDENCE_DAY = e -> substitution(e, e2 -> e2.getMonthValue() == 7 && e2.getDayOfMonth() == 4 ? "IndependenceDay" : null);
    public static final Function<LocalDate, String> LABOR_DAY = e -> e.getMonthValue() == 9 && e.getDayOfMonth() ==
            e.with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.MONDAY)).getDayOfMonth() ? "LaborDay" : null;
    public static final Function<LocalDate, String> VETERANS_DAY = e -> substitution(e, e2 -> e2.getMonthValue() == 11 && e2.getDayOfMonth() == 11 ? "VeteransDay" : null);
    public static final Function<LocalDate, String> THANKS_GIVING_DAY = e -> e.getMonthValue() == 11 && e.getDayOfMonth() ==
            e.with(TemporalAdjusters.dayOfWeekInMonth(4, DayOfWeek.THURSDAY)).getDayOfMonth() ? "ThanksgivingDay" : null;
    public static final Function<LocalDate, String> CHRISTMAS_DAY = e -> substitution(e, e2 -> e2.getMonthValue() == 12 && e2.getDayOfMonth() == 24 ? "ChristmasDay" : null);

    private static String substitution(LocalDate date, Function<LocalDate, String> logic) {
        final String apply = logic.apply(date);
        if (apply != null) {
            return apply;
        }
        LocalDate movedFrom = null;
        if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            movedFrom = date.minus(1, ChronoUnit.DAYS);
        } else if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
            movedFrom = date.plus(1, ChronoUnit.DAYS);
        }
        if (movedFrom != null) {
            final String originalHoliday = logic.apply(movedFrom);
            if (originalHoliday != null) {
                return "${" + originalHoliday + "} (observed)";
            }
        }
        return null;
    }
}

