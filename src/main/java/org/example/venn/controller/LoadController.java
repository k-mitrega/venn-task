package org.example.venn.controller;

import jakarta.validation.Valid;
import org.example.venn.controller.dto.LoadRequest;
import org.example.venn.controller.dto.LoadResponse;
import org.example.venn.service.LoadDecision;
import org.example.venn.service.LoadResult;
import org.example.venn.service.LoadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/load-attempts")
public record LoadController(
        LoadService loadService,
        LoadAttemptFileParser loadAttemptFileParser,
        LoadResponseRenderer loadResponseRenderer) {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoadResponse> processLoad(@Valid @RequestBody LoadRequest request) {
        final var loadAttempt = request.toDomain();
        return responseFor(loadService.load(loadAttempt));
    }

    @PostMapping(
            value = "/batch",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/x-ndjson")
    public ResponseEntity<String> processLoadBatch(@RequestParam("file") MultipartFile file) {
        final var results = loadService.process(loadAttemptFileParser.parse(file));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/x-ndjson"))
                .body(loadResponseRenderer.render(results));
    }

    private ResponseEntity<LoadResponse> responseFor(LoadResult result) {
        return ResponseEntity.ok(new LoadResponse(
                result.attempt().loadId().value(),
                result.attempt().customerId().value(),
                result.decision() == LoadDecision.ACCEPTED));
    }
}
