package org.example.venn.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DomainModelContractTest {

    @Test
    void convertsLoadAttemptToLoadWithTypedIdentifiers() {
        // Given
        final var attemptId = DomainId.create();
        final var time = Instant.parse("2018-01-01T00:00:00Z");
        final var attempt = new LoadAttempt(
                attemptId,
                new LoadId("1234"),
                new CustomerId("customer-1234"),
                new BigDecimal("123.45"),
                time);

        // When
        final var load = attempt.toLoad();

        // Then
        assertThat(attempt.id()).isEqualTo(attemptId);
        assertThat(attempt.loadId()).isEqualTo(new LoadId("1234"));
        assertThat(attempt.customerId()).isEqualTo(new CustomerId("customer-1234"));
        assertThat(load.id()).isNotEqualTo(attemptId);
        assertThat(load.loadAttemptId()).isEqualTo(attemptId);
        assertThat(load.customerId()).isEqualTo(attempt.customerId());
        assertThat(load.loadAmount()).isEqualTo(attempt.loadAmount());
        assertThat(load.time()).isEqualTo(time);
    }

    @Test
    void createsUsageWithCustomerAndPeriodValues() {
        // Given
        final var id = DomainId.create();
        final var customerId = new CustomerId("customer-1234");
        final var periodStart = Instant.parse("2018-01-01T00:00:00Z");
        final var time = Instant.parse("2018-01-01T12:00:00Z");

        // When
        final var usage = new Usage(
                id,
                customerId,
                periodStart,
                new BigDecimal("5000.00"),
                periodStart,
                new BigDecimal("20000.00"),
                3,
                time,
                periodStart);

        // Then
        assertThat(usage.id()).isEqualTo(id);
        assertThat(usage.customerId()).isEqualTo(customerId);
        assertThat(usage.dailyPeriodStart()).isEqualTo(periodStart);
        assertThat(usage.dailyAmountUsed()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(usage.weeklyPeriodStart()).isEqualTo(periodStart);
        assertThat(usage.weeklyAmountUsed()).isEqualTo(new BigDecimal("20000.00"));
        assertThat(usage.dailyLoadsUsed()).isEqualTo(3);
        assertThat(usage.updatedAt()).isEqualTo(time);
        assertThat(usage.createdAt()).isEqualTo(periodStart);
    }

    @Test
    void comparesIdentifierValueObjectsByTheirValues() {
        // Given
        final var firstCustomerId = new CustomerId("1234");
        final var secondCustomerId = new CustomerId("1234");
        final var firstLoadId = new LoadId("1234");
        final var secondLoadId = new LoadId("1234");

        // When
        final var customerIdsAreEqual = firstCustomerId.equals(secondCustomerId);
        final var loadIdsAreEqual = firstLoadId.equals(secondLoadId);

        // Then
        assertThat(customerIdsAreEqual).isTrue();
        assertThat(loadIdsAreEqual).isTrue();
    }
}
