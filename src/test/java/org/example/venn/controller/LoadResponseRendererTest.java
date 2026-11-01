package org.example.venn.controller;

import org.example.venn.service.LoadDecision;
import org.example.venn.service.LoadResult;
import org.example.venn.testdata.LoadAttemptTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoadResponseRendererTest implements LoadAttemptTestData {

    private LoadResponseRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new LoadResponseRenderer(
                JsonMapper.builder().findAndAddModules().build());
    }

    @Test
    void rendersAcceptedAndDeclinedAttemptsAsNdjson() {
        // Given
        final var accepted = aLoadAttempt();
        final var declined = aLoadAttempt();
        final var results = List.of(
                new LoadResult(accepted, LoadDecision.ACCEPTED),
                new LoadResult(declined, LoadDecision.DECLINED));

        // When
        final var output = renderer.render(results);

        // Then
        assertThat(output.lines()).containsExactly(
                "{\"id\":\"%s\",\"customer_id\":\"%s\",\"accepted\":true}"
                        .formatted(accepted.loadId().value(), accepted.customerId().value()),
                "{\"id\":\"%s\",\"customer_id\":\"%s\",\"accepted\":false}"
                        .formatted(declined.loadId().value(), declined.customerId().value()));
    }

    @Test
    void skipsDuplicateAttempts() {
        // Given
        final var duplicate = aLoadAttempt();
        final var accepted = aLoadAttempt();
        final var results = List.of(
                new LoadResult(duplicate, LoadDecision.DUPLICATE),
                new LoadResult(accepted, LoadDecision.ACCEPTED));

        // When
        final var output = renderer.render(results);

        // Then
        assertThat(output.lines()).containsExactly(
                "{\"id\":\"%s\",\"customer_id\":\"%s\",\"accepted\":true}"
                        .formatted(accepted.loadId().value(), accepted.customerId().value()));
    }

    @Test
    void rendersEmptyResultForEmptyBatch() {
        // When
        final var output = renderer.render(List.of());

        // Then
        assertThat(output).isEmpty();
    }
}
