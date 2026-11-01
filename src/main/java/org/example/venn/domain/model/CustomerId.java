package org.example.venn.domain.model;

import org.example.venn.domain.validation.ValidationUtils;

public record CustomerId(String value) {

    public CustomerId {
        ValidationUtils.requireNotBlank(value, "customerId");
    }
}
