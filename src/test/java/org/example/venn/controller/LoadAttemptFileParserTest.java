package org.example.venn.controller;

import org.example.venn.service.InvalidLoadAttemptException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoadAttemptFileParserTest {

    private final LoadAttemptFileParser parser = new LoadAttemptFileParser(
            JsonMapper.builder().findAndAddModules().build());

    @Test
    void parsesNonblankJsonLines() {
        // Given
        final var file = new MockMultipartFile(
                "file",
                "input.txt",
                "text/plain",
                ("{\"id\":\"load-1\",\"customer_id\":\"customer-1\","
                                + "\"load_amount\":\"$123.45\",\"time\":\"2018-01-01T00:00:00Z\"}\n"
                                + "\n"
                                + "{\"id\":\"load-2\",\"customer_id\":\"customer-2\","
                                + "\"load_amount\":\"$25.00\",\"time\":\"2018-01-02T00:00:00Z\"}")
                        .getBytes());

        // When
        final var attempts = parser.parse(file);

        // Then
        assertThat(attempts).hasSize(2);
        assertThat(attempts.getFirst().loadId().value()).isEqualTo("load-1");
        assertThat(attempts.getFirst().customerId().value()).isEqualTo("customer-1");
        assertThat(attempts.getFirst().loadAmount()).isEqualTo(new BigDecimal("123.45"));
        assertThat(attempts.getFirst().time()).isEqualTo(Instant.parse("2018-01-01T00:00:00Z"));
        assertThat(attempts.getLast().loadId().value()).isEqualTo("load-2");
    }

    @Test
    void rejectsEmptyFile() {
        // Given
        final var file = new MockMultipartFile("file", "input.txt", "text/plain", new byte[0]);

        // When
        final var action = (org.assertj.core.api.ThrowableAssert.ThrowingCallable)
                () -> parser.parse(file);

        // Then
        assertThatThrownBy(action).isInstanceOf(InvalidLoadAttemptException.class);
    }

    @Test
    void rejectsMalformedJson() {
        // Given
        final var file = new MockMultipartFile(
                "file", "input.txt", "text/plain", "not-json".getBytes());

        // When
        final var action = (org.assertj.core.api.ThrowableAssert.ThrowingCallable)
                () -> parser.parse(file);

        // Then
        assertThatThrownBy(action).isInstanceOf(InvalidLoadAttemptException.class);
    }
}
