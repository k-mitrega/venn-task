package org.example.venn.testdata;

import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.DomainId;
import org.example.venn.domain.model.LoadAttempt;
import org.example.venn.domain.model.LoadId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface LoadAttemptTestData {

    default LoadAttempt aLoadAttempt() {
        return new LoadAttempt(
                DomainId.create(),
                new LoadId("load-" + UUID.randomUUID()),
                new CustomerId("customer-" + UUID.randomUUID()),
                new BigDecimal("100.00"),
                Instant.parse("2018-01-03T12:00:00Z"));
    }

    default LoadAttempt aLoadAttemptWithLoadId(LoadId loadId) {
        final var base = aLoadAttempt();
        return new LoadAttempt(base.id(), loadId, base.customerId(), base.loadAmount(), base.time());
    }

    default LoadAttempt aLoadAttemptWithCustomer(CustomerId customerId) {
        final var base = aLoadAttempt();
        return new LoadAttempt(base.id(), base.loadId(), customerId, base.loadAmount(), base.time());
    }

    default LoadAttempt aLoadAttemptWithAmount(BigDecimal loadAmount) {
        final var base = aLoadAttempt();
        return new LoadAttempt(base.id(), base.loadId(), base.customerId(), loadAmount, base.time());
    }

    default LoadAttempt aLoadAttemptWithTime(Instant time) {
        final var base = aLoadAttempt();
        return new LoadAttempt(base.id(), base.loadId(), base.customerId(), base.loadAmount(), time);
    }

    default LoadAttempt aDuplicateLoadAttemptOf(LoadAttempt loadAttempt) {
        return new LoadAttempt(
                DomainId.create(),
                loadAttempt.loadId(),
                loadAttempt.customerId(),
                new BigDecimal("10.00"),
                Instant.parse("2018-01-04T12:00:00Z"));
    }
}
