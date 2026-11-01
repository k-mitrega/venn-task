package org.example.venn.service;

import org.example.venn.repository.LoadAttemptRepository;
import org.example.venn.repository.LoadRepository;
import org.example.venn.repository.UsageRepository;
import org.example.venn.testdata.LoadAttemptTestData;
import org.example.venn.testdata.UsageTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LoadServiceConcurrencyTest implements LoadAttemptTestData, UsageTestData {

    @Autowired
    private LoadService loadService;
    @Autowired
    private UsageRepository usageRepository;
    @Autowired
    private LoadAttemptRepository loadAttemptRepository;
    @Autowired
    private LoadRepository loadRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void failsWithConflictAndRollsBackAttemptOnConcurrentUsageCreation()
            throws Exception {
        // Given
        final var existingUsage = aUsageWithTotals(new BigDecimal("50.00"), new BigDecimal("50.00"), 1);
        final var attempt = aLoadAttemptWithCustomer(existingUsage.customerId());
        final var customer = existingUsage.customerId();
        final var winnerReady = new CountDownLatch(1);
        final var releaseWinner = new CountDownLatch(1);
        final var transactionTemplate = new TransactionTemplate(transactionManager);
        final var winner = new Thread(() -> transactionTemplate.executeWithoutResult(status -> {
            usageRepository.save(existingUsage);
            winnerReady.countDown();
            await(releaseWinner);
        }));
        winner.start();
        assertThat(winnerReady.await(5, TimeUnit.SECONDS)).isTrue();

        // When
        try (var executor = Executors.newSingleThreadExecutor()) {
            final var losingLoad = executor.submit(() -> loadService.load(attempt));
            TimeUnit.MILLISECONDS.sleep(500);
            releaseWinner.countDown();

            // Then
            assertThatThrownBy(() -> losingLoad.get(5, TimeUnit.SECONDS))
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(ConcurrentLoadConflictException.class);
        }
        winner.join(5000);

        assertThat(loadAttemptRepository.findByCustomerIdAndLoadId(
                attempt.customerId(), attempt.loadId())).isEmpty();
        assertThat(loadRepository.findByCustomerIdAndTimeRange(
                customer,
                Instant.parse("2018-01-01T00:00:00Z"),
                Instant.parse("2018-01-04T00:00:00Z"))).isEmpty();
        final var usage = usageRepository.findByCustomerId(customer).orElseThrow();
        assertThat(usage.dailyAmountUsed()).isEqualByComparingTo("50.00");
        assertThat(usage.dailyLoadsUsed()).isEqualTo(1);
    }

    @Test
    void retriesAndAcceptsOnConcurrentUsageCreation()
            throws Exception {
        // Given
        final var existingUsage = aUsageWithTotals(new BigDecimal("50.00"), new BigDecimal("50.00"), 1);
        final var attempt = aLoadAttemptWithCustomer(existingUsage.customerId());
        final var customer = existingUsage.customerId();
        final var winnerReady = new CountDownLatch(1);
        final var releaseWinner = new CountDownLatch(1);
        final var transactionTemplate = new TransactionTemplate(transactionManager);
        final var winner = new Thread(() -> transactionTemplate.executeWithoutResult(status -> {
            usageRepository.save(existingUsage);
            winnerReady.countDown();
            await(releaseWinner);
        }));
        winner.start();
        assertThat(winnerReady.await(5, TimeUnit.SECONDS)).isTrue();

        // When
        final List<LoadResult> results;
        try (var executor = Executors.newSingleThreadExecutor()) {
            final var retriedLoad = executor.submit(() -> loadService.process(List.of(attempt)));
            TimeUnit.MILLISECONDS.sleep(500);
            releaseWinner.countDown();
            results = retriedLoad.get(10, TimeUnit.SECONDS);
        }
        winner.join(5000);

        // Then
        assertThat(results).containsExactly(new LoadResult(attempt, LoadDecision.ACCEPTED));
        assertThat(loadAttemptRepository.findByCustomerIdAndLoadId(
                attempt.customerId(), attempt.loadId())).isPresent();
        final var usage = usageRepository.findByCustomerId(customer).orElseThrow();
        assertThat(usage.dailyAmountUsed()).isEqualByComparingTo("150.00");
        assertThat(usage.weeklyAmountUsed()).isEqualByComparingTo("150.00");
        assertThat(usage.dailyLoadsUsed()).isEqualTo(2);
        assertThat(loadRepository.findByCustomerIdAndTimeRange(
                customer,
                Instant.parse("2018-01-01T00:00:00Z"),
                Instant.parse("2018-01-04T00:00:00Z"))).hasSize(1);
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("latch timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }


}
