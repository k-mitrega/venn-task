package org.example.venn.service;

import org.example.venn.domain.model.Load;
import org.example.venn.domain.model.Usage;
import org.example.venn.repository.LoadAttemptRepository;
import org.example.venn.repository.LoadRepository;
import org.example.venn.testdata.LoadAttemptTestData;
import org.example.venn.testdata.UsageTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoadProcessorTest implements LoadAttemptTestData, UsageTestData {

    private LoadAttemptRepository loadAttemptRepository;
    private LoadRepository loadRepository;
    private UsageService usageService;
    private LoadProcessor processor;

    @BeforeEach
    void setUp() {
        loadAttemptRepository = mock(LoadAttemptRepository.class);
        loadRepository = mock(LoadRepository.class);
        usageService = mock(UsageService.class);
        processor = new LoadProcessor(loadAttemptRepository, loadRepository, usageService);
        when(loadAttemptRepository.findByCustomerIdAndLoadId(any(), any()))
                .thenReturn(Optional.empty());
        when(usageService.usageInLimit(any(Usage.class))).thenReturn(true);
    }

    @Test
    void acceptsLoadWithinLimitsAndSavesUsage() {
        // Given
        final var attempt = aLoadAttempt();
        when(usageService.usageFor(any(Load.class))).thenReturn(aUsageWithTotals(new BigDecimal("100.00"), new BigDecimal("100.00"), 1));

        // When
        final var decision = processor.process(attempt);

        // Then
        assertThat(decision).isEqualTo(LoadDecision.ACCEPTED);
        verify(loadAttemptRepository).save(attempt);
        verify(loadRepository).save(any(Load.class));
        verify(usageService).save(any(Usage.class));
    }

    @Test
    void declinesLoadAboveDailyLimit() {
        // Given
        final var attempt = aLoadAttemptWithAmount(new BigDecimal("5000.01"));
        when(usageService.usageFor(any(Load.class))).thenReturn(aUsageWithTotals(new BigDecimal("5000.01"), new BigDecimal("5000.01"), 1));
        when(usageService.usageInLimit(any(Usage.class))).thenReturn(false);

        // When
        final var decision = processor.process(attempt);

        // Then
        assertThat(decision).isEqualTo(LoadDecision.DECLINED);
        verify(loadAttemptRepository).save(attempt);
        verify(loadRepository, never()).save(any(Load.class));
        verify(usageService, never()).save(any(Usage.class));
    }

    @Test
    void skipsDuplicateLoadAttempt() {
        // Given
        final var attempt = aLoadAttempt();
        when(loadAttemptRepository.findByCustomerIdAndLoadId(
                attempt.customerId(), attempt.loadId()))
                .thenReturn(Optional.of(attempt));

        // When
        final var decision = processor.process(attempt);

        // Then
        assertThat(decision).isEqualTo(LoadDecision.DUPLICATE);
        verify(loadAttemptRepository, never()).save(attempt);
        verify(usageService, never()).usageFor(any(Load.class));
        verify(loadRepository, never()).save(any(Load.class));
    }
}
