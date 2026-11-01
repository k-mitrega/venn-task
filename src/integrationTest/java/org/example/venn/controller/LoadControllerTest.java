package org.example.venn.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class LoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class SingleLoad {

        @Test
        void returnsAcceptedResponse() throws Exception {
            // Given
            final var requestBody = """
                    {"id":"load-1","customer_id":"customer-1","load_amount":"$123.45","time":"2018-01-01T00:00:00Z"}
                    """;

            // When
            final var response = mockMvc.perform(post("/api/v1/load-attempts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andReturn()
                    .getResponse();

            // Then
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);
            assertThatJson(response.getContentAsString()).isEqualTo(
                    """
                    {"id":"load-1","customer_id":"customer-1","accepted":true}
                    """);
        }

        @Test
        void returnsConflictOnDuplicateLoad() throws Exception {
            // Given
            final var loadId = "load-" + UUID.randomUUID();
            final var customerId = "customer-" + UUID.randomUUID();
            final var requestBody = """
                    {"id":"%s","customer_id":"%s","load_amount":"$123.45","time":"2018-01-01T00:00:00Z"}
                    """.formatted(loadId, customerId);

            // When
            final var firstResponse = mockMvc.perform(post("/api/v1/load-attempts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andReturn()
                    .getResponse();
            final var secondResponse = mockMvc.perform(post("/api/v1/load-attempts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andReturn()
                    .getResponse();

            // Then
            assertThat(firstResponse.getStatus()).isEqualTo(200);
            assertThat(secondResponse.getStatus()).isEqualTo(409);
            assertThat(secondResponse.getContentType()).contains("application/problem+json");
            assertThatJson(secondResponse.getContentAsString())
                    .node("title").isEqualTo("Duplicate load attempt");
        }
    }

    @Nested
    class Batch {

        @Test
        void returnsProvidedBatchOutput() throws Exception {
            // Given
            final var input = readResource("Venn - Back-End - Input.txt");
            final var expectedOutput = readResource("Venn - Back-End - Output .txt");
            final var inputFile = new MockMultipartFile(
                    "file",
                    "Venn - Back-End - Input.txt",
                    "text/plain",
                    input.getBytes(StandardCharsets.UTF_8));

            // When
            final var response = mockMvc.perform(
                            multipart("/api/v1/load-attempts/batch").file(inputFile))
                    .andReturn()
                    .getResponse();

            // Then
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentType()).contains("application/x-ndjson");
            assertThat(response.getContentAsString(StandardCharsets.UTF_8))
                    .isEqualTo(normalizeLines(expectedOutput));
        }

        private String readResource(String resourceName) throws IOException {
            try (final var inputStream = LoadControllerTest.class.getResourceAsStream(
                    "/" + resourceName)) {
                if (inputStream == null) {
                    throw new IllegalArgumentException(
                            "Missing integration test resource: " + resourceName);
                }
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        private String normalizeLines(String contents) {
            return contents.lines().collect(Collectors.joining("\n"));
        }
    }
}
