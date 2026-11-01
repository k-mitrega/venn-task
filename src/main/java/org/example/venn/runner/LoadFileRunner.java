package org.example.venn.runner;

import org.example.venn.controller.LoadAttemptFileParser;
import org.example.venn.controller.LoadResponseRenderer;
import org.example.venn.service.LoadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
class LoadFileRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(LoadFileRunner.class);
    private static final String DEFAULT_OUTPUT_FILE = "output.txt";

    private final LoadAttemptFileParser loadAttemptFileParser;
    private final LoadService loadService;
    private final LoadResponseRenderer loadResponseRenderer;

    LoadFileRunner(
            LoadAttemptFileParser loadAttemptFileParser,
            LoadService loadService,
            LoadResponseRenderer loadResponseRenderer) {
        this.loadAttemptFileParser = loadAttemptFileParser;
        this.loadService = loadService;
        this.loadResponseRenderer = loadResponseRenderer;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (args.getSourceArgs().length == 0) {
            return;
        }

        final var inputFile = Path.of(args.getSourceArgs()[0]);
        final var outputFile = args.getSourceArgs().length > 1
                ? Path.of(args.getSourceArgs()[1])
                : Path.of(DEFAULT_OUTPUT_FILE);

        final var output = loadResponseRenderer.render(
                loadService.process(loadAttemptFileParser.parse(inputFile)));
        try {
            Files.writeString(outputFile, output, StandardCharsets.UTF_8);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Cannot write output file: " + outputFile, exception);
        }
        logger.info("processed {}, output written to {}",
                inputFile, outputFile);
    }
}
