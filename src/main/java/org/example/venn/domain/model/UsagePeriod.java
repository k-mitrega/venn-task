package org.example.venn.domain.model;

import org.example.venn.domain.validation.ValidationUtils;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;

public final class UsagePeriod {

    private UsagePeriod() {
    }

    public static Instant dailyPeriodStart(Instant time) {
        ValidationUtils.requireNotNull(time, "time");
        return time.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    public static Instant weeklyPeriodStart(Instant time) {
        ValidationUtils.requireNotNull(time, "time");
        return time.atZone(ZoneOffset.UTC)
                .toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();
    }
}
