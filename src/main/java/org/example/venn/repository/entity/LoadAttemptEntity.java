package org.example.venn.repository.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "load_attempts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_load_attempts_customer_external_id",
                columnNames = {"customer_id", "external_id"}))
public class LoadAttemptEntity {

    @Id
    @Column(name = "domain_id", nullable = false)
    private UUID domainId;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "load_amount", nullable = false)
    private BigDecimal loadAmount;

    @Column(name = "time", nullable = false)
    private Instant time;

    protected LoadAttemptEntity() {
    }

    public LoadAttemptEntity(
            UUID domainId,
            String externalId,
            String customerId,
            BigDecimal loadAmount,
            Instant time) {
        this.domainId = domainId;
        this.externalId = externalId;
        this.customerId = customerId;
        this.loadAmount = loadAmount;
        this.time = time;
    }

    public UUID domainId() {
        return domainId;
    }

    public String externalId() {
        return externalId;
    }

    public String customerId() {
        return customerId;
    }

    public BigDecimal loadAmount() {
        return loadAmount;
    }

    public Instant time() {
        return time;
    }
}
