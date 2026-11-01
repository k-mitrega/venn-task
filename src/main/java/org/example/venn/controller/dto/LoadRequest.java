package org.example.venn.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.DomainId;
import org.example.venn.domain.model.LoadAttempt;
import org.example.venn.domain.model.LoadId;
import org.example.venn.service.InvalidLoadAttemptException;

import java.math.BigDecimal;
import java.time.Instant;

public record LoadRequest(
        @NotBlank String id,
        @JsonProperty("customer_id") @NotBlank String customerId,
        @JsonProperty("load_amount")
        @NotBlank
        @Pattern(regexp = "^\\$?\\d+(\\.\\d{1,2})?$")
        String loadAmount,
        @NotNull Instant time) {

    public LoadAttempt toDomain() {
        if (id == null || id.isBlank()) {
            throw new InvalidLoadAttemptException("Load ID is required");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new InvalidLoadAttemptException("Customer ID is required");
        }
        if (time == null) {
            throw new InvalidLoadAttemptException("Load time is required");
        }
        if (loadAmount == null || !loadAmount.matches("^\\$?\\d+(\\.\\d{1,2})?$")) {
            throw new InvalidLoadAttemptException("Load amount must be a valid positive amount");
        }
        try {
            final var amount = new BigDecimal(loadAmount.startsWith("$")
                    ? loadAmount.substring(1)
                    : loadAmount);
            return new LoadAttempt(
                    DomainId.create(), new LoadId(id), new CustomerId(customerId), amount, time);
        } catch (IllegalArgumentException exception) {
            if (exception instanceof InvalidLoadAttemptException invalidLoadAttemptException) {
                throw invalidLoadAttemptException;
            }
            throw new InvalidLoadAttemptException("Load amount must be a valid positive amount");
        }
    }
}
