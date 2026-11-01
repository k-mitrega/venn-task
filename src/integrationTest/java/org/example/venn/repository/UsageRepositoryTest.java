package org.example.venn.repository;

import org.example.venn.domain.model.CustomerId;
import org.example.venn.testdata.UsageTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
class UsageRepositoryTest implements UsageTestData {

    @Autowired
    private UsageRepository repository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void savesAndFindsUsageForUpdate() {
        // Given
        final var usage = aUsage();

        // When
        final var saved = repository.save(usage);
        final var found = new TransactionTemplate(transactionManager).execute(
                status -> repository.findByCustomerIdForUpdate(usage.customerId()));

        // Then
        assertThat(saved).isEqualTo(usage);
        assertThat(found).contains(usage);
    }

    @Test
    void rejectsDuplicateCustomerUsage() {
        // Given
        final var usage = aUsage();
        repository.save(usage);

        // When
        final var failure = catchThrowable(() -> repository.save(aDuplicateUsageOf(usage)));

        // Then
        assertThat(failure).isInstanceOf(ConcurrentUsageCreationException.class);
    }

    @Test
    void returnsEmptyForUnknownCustomer() {
        // Given
        final var unknownCustomer = new CustomerId("unknown-customer-" + UUID.randomUUID());

        // When
        final var found = repository.findByCustomerId(unknownCustomer);

        // Then
        assertThat(found).isEmpty();
    }
}
