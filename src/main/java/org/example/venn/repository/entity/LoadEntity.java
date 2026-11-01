package org.example.venn.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loads")
public class LoadEntity {

    @Id
    @Column(name = "domain_id", nullable = false)
    private UUID domainId;

    @Column(name = "load_attempt_domain_id", nullable = false)
    private UUID loadAttemptDomainId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "load_amount", nullable = false)
    private BigDecimal loadAmount;

    @Column(name = "time", nullable = false)
    private Instant time;

    protected LoadEntity() {
    }

    public LoadEntity(
            UUID domainId,
            UUID loadAttemptDomainId,
            String customerId,
            BigDecimal loadAmount,
            Instant time) {
        this.domainId = domainId;
        this.loadAttemptDomainId = loadAttemptDomainId;
        this.customerId = customerId;
        this.loadAmount = loadAmount;
        this.time = time;
    }

    public UUID domainId() {
        return domainId;
    }

    public UUID loadAttemptDomainId() {
        return loadAttemptDomainId;
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
