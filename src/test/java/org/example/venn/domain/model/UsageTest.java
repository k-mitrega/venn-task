package org.example.venn.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UsageTest {

    @Test
    void tracksUsageAfterFirstLoad() {
        // Given
        final var load = load("2018-01-03T12:00:00Z", "100.00");

        // When
        final var usage = Usage.newUsageFromLoad(load);

        // Then
        assertThat(usage.dailyPeriodStart()).isEqualTo(Instant.parse("2018-01-03T00:00:00Z"));
        assertThat(usage.weeklyPeriodStart()).isEqualTo(Instant.parse("2018-01-01T00:00:00Z"));
        assertThat(usage.dailyAmountUsed()).isEqualTo(new BigDecimal("100.00"));
        assertThat(usage.weeklyAmountUsed()).isEqualTo(new BigDecimal("100.00"));
        assertThat(usage.dailyLoadsUsed()).isEqualTo(1);
        assertThat(usage.createdAt()).isEqualTo(load.time());
        assertThat(usage.updatedAt()).isEqualTo(load.time());
    }

    @Test
    void addsUsageWithinCurrentPeriodsAndReturnsCopy() {
        // Given
        final var firstLoad = load("2018-01-03T12:00:00Z", "100.00");
        final var existing = Usage.newUsageFromLoad(firstLoad);
        final var nextLoad = load("2018-01-03T13:00:00Z", "200.00");

        // When
        final var updated = existing.updateUsage(nextLoad);

        // Then
        assertThat(updated).isNotSameAs(existing);
        assertThat(updated.id()).isEqualTo(existing.id());
        assertThat(updated.createdAt()).isEqualTo(existing.createdAt());
        assertThat(updated.dailyAmountUsed()).isEqualTo(new BigDecimal("300.00"));
        assertThat(updated.weeklyAmountUsed()).isEqualTo(new BigDecimal("300.00"));
        assertThat(updated.dailyLoadsUsed()).isEqualTo(2);
        assertThat(updated.updatedAt()).isEqualTo(nextLoad.time());
    }

    @Test
    void resetsDailyUsageAtDayBoundary() {
        // Given
        final var existing = Usage.newUsageFromLoad(load("2018-01-03T23:00:00Z", "100.00"));
        final var nextLoad = load("2018-01-04T01:00:00Z", "200.00");

        // When
        final var updated = existing.updateUsage(nextLoad);

        // Then
        assertThat(updated.dailyPeriodStart()).isEqualTo(Instant.parse("2018-01-04T00:00:00Z"));
        assertThat(updated.weeklyPeriodStart()).isEqualTo(existing.weeklyPeriodStart());
        assertThat(updated.dailyAmountUsed()).isEqualTo(new BigDecimal("200.00"));
        assertThat(updated.weeklyAmountUsed()).isEqualTo(new BigDecimal("300.00"));
        assertThat(updated.dailyLoadsUsed()).isEqualTo(1);
    }

    @Test
    void resetsDailyAndWeeklyUsageAtWeekBoundary() {
        // Given
        final var existing = Usage.newUsageFromLoad(load("2018-01-07T23:00:00Z", "100.00"));
        final var nextLoad = load("2018-01-08T01:00:00Z", "200.00");

        // When
        final var updated = existing.updateUsage(nextLoad);

        // Then
        assertThat(updated.dailyPeriodStart()).isEqualTo(Instant.parse("2018-01-08T00:00:00Z"));
        assertThat(updated.weeklyPeriodStart()).isEqualTo(Instant.parse("2018-01-08T00:00:00Z"));
        assertThat(updated.dailyAmountUsed()).isEqualTo(new BigDecimal("200.00"));
        assertThat(updated.weeklyAmountUsed()).isEqualTo(new BigDecimal("200.00"));
        assertThat(updated.dailyLoadsUsed()).isEqualTo(1);
    }

    private static Load load(String time, String amount) {
        return new Load(
                DomainId.create(),
                DomainId.create(),
                new CustomerId("customer-1"),
                new BigDecimal(amount),
                Instant.parse(time));
    }
}
