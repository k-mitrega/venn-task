package org.example.venn.testdata;

import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.DomainId;
import org.example.venn.domain.model.Load;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface LoadTestData {

    default Load aLoad() {
        return new Load(
                DomainId.create(),
                DomainId.create(),
                new CustomerId("customer-" + UUID.randomUUID()),
                new BigDecimal("100.00"),
                Instant.parse("2018-01-03T12:00:00Z"));
    }

    default Load aLoadWithLoadAttemptId(DomainId loadAttemptId) {
        final var base = aLoad();
        return new Load(base.id(), loadAttemptId, base.customerId(), base.loadAmount(), base.time());
    }

    default Load aLoadWithCustomer(CustomerId customerId) {
        final var base = aLoad();
        return new Load(base.id(), base.loadAttemptId(), customerId, base.loadAmount(), base.time());
    }

    default Load aLoadWithAmount(BigDecimal loadAmount) {
        final var base = aLoad();
        return new Load(base.id(), base.loadAttemptId(), base.customerId(), loadAmount, base.time());
    }

    default Load aLoadWithTime(Instant time) {
        final var base = aLoad();
        return new Load(base.id(), base.loadAttemptId(), base.customerId(), base.loadAmount(), time);
    }
}
