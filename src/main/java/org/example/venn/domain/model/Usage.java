package org.example.venn.domain.model;

import org.example.venn.domain.validation.ValidationUtils;

import java.math.BigDecimal;
import java.time.Instant;

public record Usage(
        DomainId id,
        CustomerId customerId,
        Instant dailyPeriodStart,
        BigDecimal dailyAmountUsed,
        Instant weeklyPeriodStart,
        BigDecimal weeklyAmountUsed,
        int dailyLoadsUsed,
        Instant updatedAt,
        Instant createdAt) {

    public Usage {
        ValidationUtils.requireNotNull(id, "id");
        ValidationUtils.requireNotNull(customerId, "customerId");
        ValidationUtils.requireNotNull(dailyPeriodStart, "dailyPeriodStart");
        ValidationUtils.requireNonNegative(dailyAmountUsed, "dailyAmountUsed");
        ValidationUtils.requireNotNull(weeklyPeriodStart, "weeklyPeriodStart");
        ValidationUtils.requireNonNegative(weeklyAmountUsed, "weeklyAmountUsed");
        ValidationUtils.requireNonNegative(dailyLoadsUsed, "dailyLoadsUsed");
        ValidationUtils.requireNotNull(updatedAt, "updatedAt");
        ValidationUtils.requireNotNull(createdAt, "createdAt");
    }

    public static Usage newUsageFromLoad(Load load) {
        ValidationUtils.requireNotNull(load, "load");
        return new Usage(
                DomainId.create(),
                load.customerId(),
                UsagePeriod.dailyPeriodStart(load.time()),
                load.loadAmount(),
                UsagePeriod.weeklyPeriodStart(load.time()),
                load.loadAmount(),
                1,
                load.time(),
                load.time());
    }

    public Usage updateUsage(Load load) {
        ValidationUtils.requireNotNull(load, "load");
        final var newDailyPeriod =
                !dailyPeriodStart.equals(UsagePeriod.dailyPeriodStart(load.time()));
        final var newWeeklyPeriod =
                !weeklyPeriodStart.equals(UsagePeriod.weeklyPeriodStart(load.time()));

        return new Usage(
                id,
                customerId,
                newDailyPeriod ? UsagePeriod.dailyPeriodStart(load.time()) : dailyPeriodStart,
                newDailyPeriod ? load.loadAmount() : dailyAmountUsed.add(load.loadAmount()),
                newWeeklyPeriod ? UsagePeriod.weeklyPeriodStart(load.time()) : weeklyPeriodStart,
                newWeeklyPeriod ? load.loadAmount() : weeklyAmountUsed.add(load.loadAmount()),
                newDailyPeriod ? 1 : dailyLoadsUsed + 1,
                load.time(),
                createdAt);
    }
}
