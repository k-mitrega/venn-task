package org.example.venn.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "venn.usage")
@Validated
public record UsageLimits(
        @Positive BigDecimal dailyLoadAmountLimit,
        @Positive BigDecimal weeklyLoadAmountLimit,
        @Positive int dailyLoadCountLimit) {
}
