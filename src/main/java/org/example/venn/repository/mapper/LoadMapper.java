package org.example.venn.repository.mapper;

import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.DomainId;
import org.example.venn.domain.model.Load;
import org.example.venn.repository.entity.LoadEntity;
import org.springframework.stereotype.Component;

@Component
public record LoadMapper() {

    public Load toDomain(LoadEntity entity) {
        return new Load(
                new DomainId(entity.domainId()),
                new DomainId(entity.loadAttemptDomainId()),
                new CustomerId(entity.customerId()),
                entity.loadAmount(),
                entity.time());
    }

    public LoadEntity toEntity(Load domain) {
        return new LoadEntity(
                domain.id().value(),
                domain.loadAttemptId().value(),
                domain.customerId().value(),
                domain.loadAmount(),
                domain.time());
    }
}
