package org.example.venn.repository;

import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.DomainId;
import org.example.venn.domain.model.Load;
import org.example.venn.domain.model.LoadAttempt;
import org.example.venn.domain.model.LoadId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoadRepositoryTest {

    @Autowired
    private LoadRepository repository;
    @Autowired
    private LoadAttemptRepository attemptRepository;

    @Test
    void savesAndFiltersLoadsByCustomerAndTimeRange() {
        // Given
        final var customerId = new CustomerId("customer-" + UUID.randomUUID());
        final var otherCustomerId = new CustomerId("other-customer-" + UUID.randomUUID());
        final var rangeStart = Instant.parse("2018-01-01T00:00:00Z");
        final var rangeEnd = Instant.parse("2018-01-02T00:00:00Z");
        final var atStart = load(customerId, "attempt-at-start", "10.00", rangeStart);
        final var insideRange = load(customerId, "attempt-inside-range", "20.00", Instant.parse("2018-01-01T12:00:00Z"));
        final var atEnd = load(customerId, "attempt-at-end", "30.00", rangeEnd);
        final var otherCustomerLoad = load(otherCustomerId, "attempt-other-customer", "40.00", rangeStart);

        // When
        final var savedAtStart = repository.save(atStart);
        final var savedInsideRange = repository.save(insideRange);
        final var savedAtEnd = repository.save(atEnd);
        final var savedOtherCustomerLoad = repository.save(otherCustomerLoad);
        final var found = repository.findByCustomerIdAndTimeRange(
                customerId, rangeStart, rangeEnd);

        // Then
        assertThat(savedAtStart).isEqualTo(atStart);
        assertThat(savedInsideRange).isEqualTo(insideRange);
        assertThat(savedAtEnd).isEqualTo(atEnd);
        assertThat(savedOtherCustomerLoad).isEqualTo(otherCustomerLoad);
        assertThat(found).containsExactlyInAnyOrder(atStart, insideRange);
    }

    private Load load(CustomerId customerId, String externalId, String amount, Instant time) {
        final var attempt = new LoadAttempt(
                DomainId.create(),
                new LoadId(externalId),
                customerId,
                new BigDecimal("1.00"),
                Instant.parse("2018-01-01T00:00:00Z"));
        attemptRepository.save(attempt);
        return new Load(
                DomainId.create(),
                attempt.id(),
                customerId,
                new BigDecimal(amount),
                time);
    }
}
