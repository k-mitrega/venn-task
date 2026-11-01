package org.example.venn.domain.model;

import org.example.venn.domain.validation.ValidationUtils;

import java.math.BigDecimal;
import java.time.Instant;

public record Load(
        DomainId id,
        DomainId loadAttemptId,
        CustomerId customerId,
        BigDecimal loadAmount,
        Instant time) {

    public Load {
        ValidationUtils.requireNotNull(id, "id");
        ValidationUtils.requireNotNull(loadAttemptId, "loadAttemptId");
        ValidationUtils.requireNotNull(customerId, "customerId");
        ValidationUtils.requirePositive(loadAmount, "loadAmount");
        ValidationUtils.requireNotNull(time, "time");
    }
}
