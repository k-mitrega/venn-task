package org.example.venn.service;

import org.example.venn.repository.ConcurrentUsageCreationException;
import org.example.venn.repository.DuplicateLoadAttemptException;
import org.example.venn.testdata.LoadAttemptTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

class LoadServiceTest implements LoadAttemptTestData {

    private LoadProcessor loadProcessor;
    private LoadService service;

    @BeforeEach
    void setUp() {
        loadProcessor = mock(LoadProcessor.class);
        service = new LoadService(loadProcessor);
    }

    @Test
    void returnsResultWithAttemptAndDecisionFromProcessor() {
        // Given
        final var attempt = aLoadAttempt();
        when(loadProcessor.process(attempt)).thenReturn(LoadDecision.ACCEPTED);

        // When
        final var result = service.load(attempt);

        // Then
        assertThat(result).isEqualTo(new LoadResult(attempt, LoadDecision.ACCEPTED));
    }

    @Test
    void mapsPreCheckedDuplicateToDuplicateConflict() {
        // Given
        final var attempt = aLoadAttempt();
        when(loadProcessor.process(attempt)).thenReturn(LoadDecision.DUPLICATE);

        // When
        final var failure = catchThrowable(() -> service.load(attempt));

        // Then
        assertThat(failure).isInstanceOf(DuplicateLoadConflictException.class);
    }

    @Test
    void mapsDuplicatePersistenceRaceToDuplicateConflict() {
        // Given
        final var attempt = aLoadAttempt();
        when(loadProcessor.process(attempt))
                .thenThrow(new DuplicateLoadAttemptException(null));

        // When
        final var failure = catchThrowable(() -> service.load(attempt));

        // Then
        assertThat(failure).isInstanceOf(DuplicateLoadConflictException.class);
    }

    @Test
    void mapsUsageCreationRaceToConcurrentLoadConflict() {
        // Given
        final var attempt = aLoadAttempt();
        when(loadProcessor.process(attempt))
                .thenThrow(new ConcurrentUsageCreationException(null));

        // When
        final var failure = catchThrowable(() -> service.load(attempt));

        // Then
        assertThat(failure).isInstanceOf(ConcurrentLoadConflictException.class);
    }

    @Test
    void propagatesUnexpectedProcessorFailure() {
        // Given
        final var attempt = aLoadAttempt();
        when(loadProcessor.process(attempt)).thenThrow(new IllegalStateException("boom"));

        // When
        final var failure = catchThrowable(() -> service.load(attempt));

        // Then
        assertThat(failure).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void returnsEmptyResultForEmptyBatch() {
        // When
        final var results = service.process(List.of());

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void returnsResultPerRowIncludingDuplicates() {
        // Given
        final var accepted = aLoadAttempt();
        final var duplicate = aLoadAttempt();
        final var declined = aLoadAttempt();
        when(loadProcessor.process(accepted)).thenReturn(LoadDecision.ACCEPTED);
        when(loadProcessor.process(duplicate)).thenReturn(LoadDecision.DUPLICATE);
        when(loadProcessor.process(declined)).thenReturn(LoadDecision.DECLINED);

        // When
        final var results = service.process(List.of(accepted, duplicate, declined));

        // Then
        assertThat(results).containsExactly(
                new LoadResult(accepted, LoadDecision.ACCEPTED),
                new LoadResult(duplicate, LoadDecision.DUPLICATE),
                new LoadResult(declined, LoadDecision.DECLINED));
    }

    @Test
    void retriesRowThatLosesUsageCreationRaceOnce() {
        // Given
        final var raced = aLoadAttempt();
        when(loadProcessor.process(raced))
                .thenThrow(new ConcurrentUsageCreationException(null))
                .thenReturn(LoadDecision.ACCEPTED);

        // When
        final var results = service.process(List.of(raced));

        // Then
        assertThat(results).containsExactly(new LoadResult(raced, LoadDecision.ACCEPTED));
        verify(loadProcessor, times(2)).process(raced);
    }

    @Test
    void propagatesConflictWhenRowLosesRaceTwice() {
        // Given
        final var raced = aLoadAttempt();
        when(loadProcessor.process(raced))
                .thenThrow(new ConcurrentUsageCreationException(null))
                .thenThrow(new ConcurrentUsageCreationException(null));

        // When
        final var failure = catchThrowable(() -> service.process(List.of(raced)));

        // Then
        assertThat(failure).isInstanceOf(ConcurrentLoadConflictException.class);
        verify(loadProcessor, times(2)).process(raced);
    }

    @Test
    void doesNotRetryRowsWithoutConflict() {
        // Given
        final var duplicate = aLoadAttempt();
        when(loadProcessor.process(duplicate)).thenReturn(LoadDecision.DUPLICATE);

        // When
        final var results = service.process(List.of(duplicate));

        // Then
        assertThat(results).containsExactly(new LoadResult(duplicate, LoadDecision.DUPLICATE));
        verify(loadProcessor, times(1)).process(duplicate);
    }

    @Test
    void propagatesUnexpectedFailureWithoutRetry() {
        // Given
        final var failing = aLoadAttempt();
        when(loadProcessor.process(failing)).thenThrow(new IllegalStateException("boom"));

        // When
        final var failure = catchThrowable(() -> service.process(List.of(failing)));

        // Then
        assertThat(failure).isInstanceOf(IllegalStateException.class);
        verify(loadProcessor, times(1)).process(failing);
    }

}
