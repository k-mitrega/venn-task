package org.example.venn.testdata;

import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.DomainId;
import org.example.venn.domain.model.Usage;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface UsageTestData {

    default Usage aUsage() {
        return new Usage(
                DomainId.create(),
                new CustomerId("customer-" + UUID.randomUUID()),
                Instant.parse("2018-01-03T00:00:00Z"),
                new BigDecimal("100.00"),
                Instant.parse("2018-01-01T00:00:00Z"),
                new BigDecimal("100.00"),
                1,
                Instant.parse("2018-01-03T12:00:00Z"),
                Instant.parse("2018-01-03T12:00:00Z"));
    }

    default Usage aUsageWithCustomer(CustomerId customerId) {
        final var base = aUsage();
        return new Usage(
                base.id(),
                customerId,
                base.dailyPeriodStart(),
                base.dailyAmountUsed(),
                base.weeklyPeriodStart(),
                base.weeklyAmountUsed(),
                base.dailyLoadsUsed(),
                base.updatedAt(),
                base.createdAt());
    }

    default Usage aUsageWithTotals(
            BigDecimal dailyAmountUsed, BigDecimal weeklyAmountUsed, int dailyLoadsUsed) {
        final var base = aUsage();
        return new Usage(
                base.id(),
                base.customerId(),
                base.dailyPeriodStart(),
                dailyAmountUsed,
                base.weeklyPeriodStart(),
                weeklyAmountUsed,
                dailyLoadsUsed,
                base.updatedAt(),
                base.createdAt());
    }

    default Usage aDuplicateUsageOf(Usage usage) {
        return new Usage(
                DomainId.create(),
                usage.customerId(),
                usage.dailyPeriodStart(),
                usage.dailyAmountUsed().add(usage.dailyAmountUsed()),
                usage.weeklyPeriodStart(),
                usage.weeklyAmountUsed().add(usage.weeklyAmountUsed()),
                usage.dailyLoadsUsed() + 1,
                usage.updatedAt(),
                usage.createdAt());
    }
}
