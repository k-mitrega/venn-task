package org.example.venn.domain.model;

import org.example.venn.domain.validation.ValidationUtils;

import java.math.BigDecimal;
import java.time.Instant;

public record LoadAttempt(
        DomainId id,
        LoadId loadId,
        CustomerId customerId,
        BigDecimal loadAmount,
        Instant time) {

    public LoadAttempt {
        ValidationUtils.requireNotNull(id, "id");
        ValidationUtils.requireNotNull(loadId, "loadId");
        ValidationUtils.requireNotNull(customerId, "customerId");
        ValidationUtils.requirePositive(loadAmount, "loadAmount");
        ValidationUtils.requireNotNull(time, "time");
    }

    public Load toLoad() {
        return new Load(DomainId.create(), id, customerId, loadAmount, time);
    }
}
