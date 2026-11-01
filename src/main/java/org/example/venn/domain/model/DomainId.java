package org.example.venn.domain.model;

import org.example.venn.domain.validation.ValidationUtils;

import java.util.UUID;

public record DomainId(UUID value) {

    public DomainId {
        ValidationUtils.requireNotNull(value, "value");
    }

    public static DomainId create() {
        return new DomainId(UUID.randomUUID());
    }
}
