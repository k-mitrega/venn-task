package org.example.venn.repository;

import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.LoadId;
import org.example.venn.testdata.LoadAttemptTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
class LoadAttemptRepositoryTest implements LoadAttemptTestData {

    @Autowired
    private LoadAttemptRepository repository;

    @Test
    void persistsAndFindsLoadAttempt() {
        // Given
        final var loadAttempt = aLoadAttempt();

        // When
        final var saved = repository.save(loadAttempt);
        final var found = repository.findByCustomerIdAndLoadId(
                loadAttempt.customerId(), loadAttempt.loadId());

        // Then
        assertThat(saved).isEqualTo(loadAttempt);
        assertThat(found).contains(loadAttempt);
    }

    @Test
    void rejectsDuplicateCustomerAndLoadId() {
        // Given
        final var loadAttempt = aLoadAttempt();
        repository.save(loadAttempt);

        // When
        final var failure = catchThrowable(() -> repository.save(aDuplicateLoadAttemptOf(loadAttempt)));

        // Then
        assertThat(failure).isInstanceOf(DuplicateLoadAttemptException.class);
    }

    @Test
    void returnsEmptyForUnknownLoadAttempt() {
        // Given
        final var unknownCustomer = new CustomerId("unknown-customer-" + UUID.randomUUID());
        final var unknownLoad = new LoadId("unknown-load-" + UUID.randomUUID());

        // When
        final var found = repository.findByCustomerIdAndLoadId(unknownCustomer, unknownLoad);

        // Then
        assertThat(found).isEmpty();
    }
}
