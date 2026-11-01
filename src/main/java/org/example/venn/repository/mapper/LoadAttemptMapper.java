package org.example.venn.repository.mapper;

import org.example.venn.domain.model.CustomerId;
import org.example.venn.domain.model.DomainId;
import org.example.venn.domain.model.LoadAttempt;
import org.example.venn.domain.model.LoadId;
import org.example.venn.repository.entity.LoadAttemptEntity;
import org.springframework.stereotype.Component;

@Component
public record LoadAttemptMapper() {

    public LoadAttempt toDomain(LoadAttemptEntity entity) {
        return new LoadAttempt(
                new DomainId(entity.domainId()),
                new LoadId(entity.externalId()),
                new CustomerId(entity.customerId()),
                entity.loadAmount(),
                entity.time());
    }

    public LoadAttemptEntity toEntity(LoadAttempt domain) {
        return new LoadAttemptEntity(
                domain.id().value(),
                domain.loadId().value(),
                domain.customerId().value(),
                domain.loadAmount(),
                domain.time());
    }
}
