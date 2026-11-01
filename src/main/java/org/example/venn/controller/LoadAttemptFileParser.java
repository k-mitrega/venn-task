package org.example.venn.controller;

import org.example.venn.controller.dto.LoadRequest;
import org.example.venn.domain.model.LoadAttempt;
import org.example.venn.service.InvalidLoadAttemptException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class LoadAttemptFileParser {

    private final ObjectMapper objectMapper;

    public LoadAttemptFileParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<LoadAttempt> parse(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidLoadAttemptException("Batch file must not be empty");
        }
        try {
            return parseContents(new String(file.getBytes(), StandardCharsets.UTF_8));
        } catch (IOException | JacksonException exception) {
            throw new InvalidLoadAttemptException("Batch file contains unreadable load data");
        }
    }

    public List<LoadAttempt> parse(Path file) {
        try {
            return parseContents(Files.readString(file, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new InvalidLoadAttemptException("Batch file contains unreadable load data");
        }
    }

    private List<LoadAttempt> parseContents(String contents) {
        final var loadAttempts = new ArrayList<LoadAttempt>();
        for (final var line : contents.split("\\R")) {
            if (line.isBlank()) {
                continue;
            }
            final var request = objectMapper.readValue(line, LoadRequest.class);
            loadAttempts.add(request.toDomain());
        }
        return List.copyOf(loadAttempts);
    }
}
