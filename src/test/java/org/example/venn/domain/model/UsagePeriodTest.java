package org.example.venn.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UsagePeriodTest {

    @Test
    void calculatesUtcStartOfDay() {
        // Given
        final var instant = Instant.parse("2018-01-03T23:59:59Z");

        // When
        final var dailyStart = UsagePeriod.dailyPeriodStart(instant);

        // Then
        assertThat(dailyStart).isEqualTo(Instant.parse("2018-01-03T00:00:00Z"));
    }

    @Test
    void calculatesMondayStartOfWeekInUtc() {
        // Given
        final var sunday = Instant.parse("2018-01-07T23:59:59Z");
        final var monday = Instant.parse("2018-01-08T00:00:00Z");

        // When
        final var sundayWeekStart = UsagePeriod.weeklyPeriodStart(sunday);
        final var mondayWeekStart = UsagePeriod.weeklyPeriodStart(monday);

        // Then
        assertThat(sundayWeekStart).isEqualTo(Instant.parse("2018-01-01T00:00:00Z"));
        assertThat(mondayWeekStart).isEqualTo(monday);
    }
}
