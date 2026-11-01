package org.example.venn.repository.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "customer_usage",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_customer_usage_customer_id",
                columnNames = {"customer_id"}))
public class UsageEntity {

    @Id
    @Column(name = "domain_id", nullable = false)
    private UUID domainId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "daily_period_start", nullable = false)
    private Instant dailyPeriodStart;

    @Column(name = "daily_amount_used", nullable = false)
    private BigDecimal dailyAmountUsed;

    @Column(name = "weekly_period_start", nullable = false)
    private Instant weeklyPeriodStart;

    @Column(name = "weekly_amount_used", nullable = false)
    private BigDecimal weeklyAmountUsed;

    @Column(name = "daily_loads_used", nullable = false)
    private Integer dailyLoadsUsed;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected UsageEntity() {
    }

    public UsageEntity(
            UUID domainId,
            String customerId,
            Instant dailyPeriodStart,
            BigDecimal dailyAmountUsed,
            Instant weeklyPeriodStart,
            BigDecimal weeklyAmountUsed,
            Integer dailyLoadsUsed,
            Instant updatedAt,
            Instant createdAt) {
        this.domainId = domainId;
        this.customerId = customerId;
        this.dailyPeriodStart = dailyPeriodStart;
        this.dailyAmountUsed = dailyAmountUsed;
        this.weeklyPeriodStart = weeklyPeriodStart;
        this.weeklyAmountUsed = weeklyAmountUsed;
        this.dailyLoadsUsed = dailyLoadsUsed;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public UUID domainId() {
        return domainId;
    }

    public String customerId() {
        return customerId;
    }

    public Instant dailyPeriodStart() {
        return dailyPeriodStart;
    }

    public BigDecimal dailyAmountUsed() {
        return dailyAmountUsed;
    }

    public Instant weeklyPeriodStart() {
        return weeklyPeriodStart;
    }

    public BigDecimal weeklyAmountUsed() {
        return weeklyAmountUsed;
    }

    public Integer dailyLoadsUsed() {
        return dailyLoadsUsed;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
