package org.example.venn.domain.model;

import org.example.venn.domain.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class DomainValidationTest {

    @Test
    void rejectsNullDomainIdentifier() {
        // Given
        final var invalidId = (Runnable) () -> new DomainId(null);

        // When
        final var failure = catchThrowable(invalidId::run);

        // Then
        assertThat(failure).isInstanceOf(DomainValidationException.class);
    }

    @Test
    void rejectsBlankCustomerIdentifier() {
        // Given
        final var invalidId = (Runnable) () -> new CustomerId(" ");

        // When
        final var failure = catchThrowable(invalidId::run);

        // Then
        assertThat(failure).isInstanceOf(DomainValidationException.class);
    }

    @Test
    void rejectsBlankLoadIdentifier() {
        // Given
        final var invalidId = (Runnable) () -> new LoadId("");

        // When
        final var failure = catchThrowable(invalidId::run);

        // Then
        assertThat(failure).isInstanceOf(DomainValidationException.class);
    }

    @Test
    void rejectsInvalidLoadAttemptAndLoadValues() {
        // Given
        final var loadId = new LoadId("load-1");
        final var customerId = new CustomerId("customer-1");
        final var domainId = DomainId.create();
        final var time = Instant.parse("2018-01-01T00:00:00Z");

        // When
        final var missingAttemptId = catchThrowable(() -> new LoadAttempt(
                null, loadId, customerId, BigDecimal.ONE, time));
        final var nonPositiveAttemptAmount = catchThrowable(() -> new LoadAttempt(
                domainId, loadId, customerId, BigDecimal.ZERO, time));
        final var missingAttemptTime = catchThrowable(() -> new LoadAttempt(
                domainId, loadId, customerId, BigDecimal.ONE, null));
        final var nonPositiveLoadAmount = catchThrowable(() -> new Load(
                domainId, domainId, customerId, BigDecimal.ZERO, time));

        // Then
        assertThat(missingAttemptId).isInstanceOf(DomainValidationException.class);
        assertThat(nonPositiveAttemptAmount).isInstanceOf(DomainValidationException.class);
        assertThat(missingAttemptTime).isInstanceOf(DomainValidationException.class);
        assertThat(nonPositiveLoadAmount).isInstanceOf(DomainValidationException.class);
    }

    @Test
    void rejectsInvalidUsageAndPeriodValues() {
        // Given
        final var id = DomainId.create();
        final var customerId = new CustomerId("customer-1");
        final var time = Instant.parse("2018-01-01T00:00:00Z");

        // When
        final var missingDailyPeriod = catchThrowable(() -> new Usage(
                id, customerId, null, BigDecimal.ZERO, time, BigDecimal.ZERO, 0, time, time));
        final var negativeDailyUsage = catchThrowable(() -> new Usage(
                id, customerId, time, new BigDecimal("-0.01"), time, BigDecimal.ZERO, 0, time, time));
        final var missingDailyStart = catchThrowable(() -> UsagePeriod.dailyPeriodStart(null));
        final var missingWeeklyStart = catchThrowable(() -> UsagePeriod.weeklyPeriodStart(null));

        // Then
        assertThat(missingDailyPeriod).isInstanceOf(DomainValidationException.class);
        assertThat(negativeDailyUsage).isInstanceOf(DomainValidationException.class);
        assertThat(missingDailyStart).isInstanceOf(DomainValidationException.class);
        assertThat(missingWeeklyStart).isInstanceOf(DomainValidationException.class);
    }
}
