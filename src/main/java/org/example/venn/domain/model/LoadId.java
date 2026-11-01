package org.example.venn.domain.model;

import org.example.venn.domain.validation.ValidationUtils;

public record LoadId(String value) {

    public LoadId {
        ValidationUtils.requireNotBlank(value, "loadId");
    }
}
