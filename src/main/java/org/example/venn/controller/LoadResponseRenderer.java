package org.example.venn.controller;

import org.example.venn.controller.dto.LoadResponse;
import org.example.venn.service.LoadDecision;
import org.example.venn.service.LoadResult;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoadResponseRenderer {

    private final ObjectMapper objectMapper;

    public LoadResponseRenderer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String render(List<LoadResult> results) {
        return results.stream()
                .filter(result -> result.decision() != LoadDecision.DUPLICATE)
                .map(result -> objectMapper.writeValueAsString(toResponse(result)))
                .collect(Collectors.joining("\n"));
    }

    private LoadResponse toResponse(LoadResult result) {
        return new LoadResponse(
                result.attempt().loadId().value(),
                result.attempt().customerId().value(),
                result.decision() == LoadDecision.ACCEPTED);
    }
}
