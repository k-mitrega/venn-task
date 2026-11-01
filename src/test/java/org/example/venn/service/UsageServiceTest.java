package org.example.venn.service;

import org.example.venn.config.UsageLimits;
import org.example.venn.repository.UsageRepository;
import org.example.venn.testdata.UsageTestData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UsageServiceTest implements UsageTestData {

    @Test
    void appliesConfiguredAmountAndCountLimits() {
        // Given
        final var service = new UsageService(
                mock(UsageRepository.class),
                new UsageLimits(
                        new BigDecimal("100.00"),
                        new BigDecimal("200.00"),
                        2));
        final var withinLimits = aUsageWithTotals(new BigDecimal("100.00"), new BigDecimal("200.00"), 2);
        final var exceedsDailyLimit = aUsageWithTotals(new BigDecimal("100.01"), new BigDecimal("200.00"), 2);
        final var exceedsWeeklyLimit = aUsageWithTotals(new BigDecimal("100.00"), new BigDecimal("200.01"), 2);
        final var exceedsCountLimit = aUsageWithTotals(new BigDecimal("100.00"), new BigDecimal("200.00"), 3);

        // When
        final var withinResult = service.usageInLimit(withinLimits);
        final var dailyLimitResult = service.usageInLimit(exceedsDailyLimit);
        final var weeklyLimitResult = service.usageInLimit(exceedsWeeklyLimit);
        final var countLimitResult = service.usageInLimit(exceedsCountLimit);

        // Then
        assertThat(withinResult).isTrue();
        assertThat(dailyLimitResult).isFalse();
        assertThat(weeklyLimitResult).isFalse();
        assertThat(countLimitResult).isFalse();
    }

}
