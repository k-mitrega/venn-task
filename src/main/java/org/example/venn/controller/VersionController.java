package org.example.venn.controller;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class VersionController {

    private final BuildProperties buildProperties;

    VersionController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping("/version")
    Map<String, String> version() {
        return Map.of(
                "name", buildProperties.getName(),
                "version", buildProperties.getVersion());
    }
}
