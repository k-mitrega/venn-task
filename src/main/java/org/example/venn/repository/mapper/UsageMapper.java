package org.example.venn.repository.mapper;

import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.DomainId;
import org.example.venn.domain.model.Usage;
import org.example.venn.repository.entity.UsageEntity;
import org.springframework.stereotype.Component;

@Component
public record UsageMapper() {

    public Usage toDomain(UsageEntity entity) {
        return new Usage(
                new DomainId(entity.domainId()),
                new CustomerId(entity.customerId()),
                entity.dailyPeriodStart(),
                entity.dailyAmountUsed(),
                entity.weeklyPeriodStart(),
                entity.weeklyAmountUsed(),
                entity.dailyLoadsUsed(),
                entity.updatedAt(),
                entity.createdAt());
    }

    public UsageEntity toEntity(Usage domain) {
        return new UsageEntity(
                domain.id().value(),
                domain.customerId().value(),
                domain.dailyPeriodStart(),
                domain.dailyAmountUsed(),
                domain.weeklyPeriodStart(),
                domain.weeklyAmountUsed(),
                domain.dailyLoadsUsed(),
                domain.updatedAt(),
                domain.createdAt());
    }
}
