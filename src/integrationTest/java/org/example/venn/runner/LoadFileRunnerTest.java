package org.example.venn.runner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LoadFileRunnerTest {

    @Autowired
    private LoadFileRunner loadFileRunner;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from loads");
        jdbcTemplate.update("delete from load_attempts");
        jdbcTemplate.update("delete from customer_usage");
    }

    @Test
    void processesInputFileAndWritesOutputFile(@TempDir Path tempDir) throws IOException {
        // Given
        final var inputFile = copyResource("Venn - Back-End - Input.txt", tempDir);
        final var outputFile = tempDir.resolve("output.txt");

        // When
        loadFileRunner.run(
                new DefaultApplicationArguments(
                        inputFile.toString(), outputFile.toString()));

        // Then
        assertThat(outputFile).exists();
        assertThat(Files.readString(outputFile, StandardCharsets.UTF_8))
                .isEqualTo(normalizeLines(readResource("Venn - Back-End - Output .txt")));
    }

    @Test
    void doesNothingWhenNoArgumentsProvided(@TempDir Path tempDir) {
        // Given
        final var outputFile = tempDir.resolve("output.txt");

        // When
        loadFileRunner.run(new DefaultApplicationArguments());

        // Then
        assertThat(outputFile).doesNotExist();
    }

    private Path copyResource(String resourceName, Path tempDir) throws IOException {
        final var target = tempDir.resolve(resourceName);
        Files.write(target, readResourceBytes(resourceName));
        return target;
    }

    private byte[] readResourceBytes(String resourceName) throws IOException {
        try (final var inputStream = getClass().getResourceAsStream("/" + resourceName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException(
                        "Missing integration test resource: " + resourceName);
            }
            return inputStream.readAllBytes();
        }
    }

    private String readResource(String resourceName) throws IOException {
        return new String(readResourceBytes(resourceName), StandardCharsets.UTF_8);
    }

    private String normalizeLines(String contents) {
        return contents.lines().collect(Collectors.joining("\n"));
    }
}
