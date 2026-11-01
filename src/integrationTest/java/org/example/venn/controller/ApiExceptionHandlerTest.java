package org.example.venn.controller;

import org.example.venn.domain.model.LoadAttempt;
import org.example.venn.service.*;
import org.example.venn.testdata.LoadAttemptTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class ApiExceptionHandlerTest implements LoadAttemptTestData {

    private LoadService loadService;
    private MockMvc mockMvc;
    private final JsonMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    @BeforeEach
    void setUp() {
        loadService = mock(LoadService.class);
        final var controller = new LoadController(
                loadService,
                mock(LoadAttemptFileParser.class),
                new LoadResponseRenderer(objectMapper));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void returnsValidationProblemForInvalidRequest() throws Exception {
        // Given
        final var requestBody = """
                {"id":"load-1","customer_id":"customer-1","load_amount":"invalid","time":"2018-01-01T00:00:00Z"}
                """;

        // When
        final var response = mockMvc.perform(post("/api/v1/load-attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn()
                .getResponse();
        final var problem = objectMapper.readTree(response.getContentAsString());

        // Then
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getContentType()).contains("application/problem+json");
        assertThat(problem.path("title").asString()).isEqualTo("Validation failed");
        assertThat(problem.path("errors").isArray()).isTrue();
    }

    @Test
    void returnsConflictProblemForConcurrentLoadConflict() throws Exception {
        // Given
        when(loadService.load(any(LoadAttempt.class)))
                .thenThrow(new ConcurrentLoadConflictException(null));
        final var requestBody = """
                {"id":"load-1","customer_id":"customer-1","load_amount":"$123.45","time":"2018-01-01T00:00:00Z"}
                """;

        // When
        final var response = mockMvc.perform(post("/api/v1/load-attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn()
                .getResponse();
        final var problem = objectMapper.readTree(response.getContentAsString());

        // Then
        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getContentType()).contains("application/problem+json");
        assertThat(problem.path("title").asString()).isEqualTo("Concurrent load conflict");
    }

    @Test
    void returnsConflictProblemForDuplicateLoadAttempt() throws Exception {
        // Given
        when(loadService.load(any(LoadAttempt.class)))
                .thenThrow(new DuplicateLoadConflictException());
        final var requestBody = """
                {"id":"load-1","customer_id":"customer-1","load_amount":"$123.45","time":"2018-01-01T00:00:00Z"}
                """;

        // When
        final var response = mockMvc.perform(post("/api/v1/load-attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn()
                .getResponse();
        final var problem = objectMapper.readTree(response.getContentAsString());

        // Then
        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getContentType()).contains("application/problem+json");
        assertThat(problem.path("title").asString()).isEqualTo("Duplicate load attempt");
    }

    @Test
    void returnsSanitizedProblemForUnexpectedException() throws Exception {
        // Given
        when(loadService.load(any(LoadAttempt.class))).thenThrow(new IllegalStateException("secret"));
        final var requestBody = """
                {"id":"load-1","customer_id":"customer-1","load_amount":"$123.45","time":"2018-01-01T00:00:00Z"}
                """;

        // When
        final var response = mockMvc.perform(post("/api/v1/load-attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn()
                .getResponse();
        final var problem = objectMapper.readTree(response.getContentAsString());

        // Then
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getContentType()).contains("application/problem+json");
        assertThat(problem.path("title").asString()).isEqualTo("Internal server error");
        assertThat(problem.path("detail").asString())
                .isEqualTo("An unexpected error occurred. Please contact support.");
        assertThat(response.getContentAsString()).doesNotContain("secret");
    }

    @Test
    void mapsDomainValidationFailureToInternalServerError() throws Exception {
        // Given
        final var requestBody = """
                {"id":"load-1","customer_id":"customer-1","load_amount":"$0","time":"2018-01-01T00:00:00Z"}
                """;

        // When
        final var response = mockMvc.perform(post("/api/v1/load-attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn()
                .getResponse();
        final var problem = objectMapper.readTree(response.getContentAsString());

        // Then
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getContentType()).contains("application/problem+json");
        assertThat(problem.path("title").asString()).isEqualTo("Internal server error");
    }

    @Test
    void returnsChallengeResponseForAcceptedLoad() throws Exception {
        // Given
        final var attempt = aLoadAttempt();
        when(loadService.load(any(LoadAttempt.class)))
                .thenReturn(new LoadResult(attempt, LoadDecision.ACCEPTED));
        final var requestBody = """
                {"id":"%s","customer_id":"%s","load_amount":"$123.45","time":"2018-01-01T00:00:00Z"}
                """.formatted(attempt.loadId().value(), attempt.customerId().value());

        // When
        final var response = mockMvc.perform(post("/api/v1/load-attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn()
                .getResponse();
        final var body = objectMapper.readTree(response.getContentAsString());

        // Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(body.path("id").asString()).isEqualTo(attempt.loadId().value());
        assertThat(body.path("customer_id").asString()).isEqualTo(attempt.customerId().value());
        assertThat(body.path("accepted").asBoolean()).isTrue();
    }
}
