package org.example.venn.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoadResponse(
        String id,
        @JsonProperty("customer_id") String customerId,
        boolean accepted) {}
