package org.example.venn.domain.validation;

import java.math.BigDecimal;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static <T> T requireNotNull(T value, String name) {
        if (value == null) {
            throw new DomainValidationException(name + " must not be null");
        }
        return value;
    }

    public static String requireNotBlank(String value, String name) {
        requireNotNull(value, name);
        if (value.isBlank()) {
            throw new DomainValidationException(name + " must not be blank");
        }
        return value;
    }

    public static BigDecimal requirePositive(BigDecimal value, String name) {
        requireNotNull(value, name);
        if (value.signum() <= 0) {
            throw new DomainValidationException(name + " must be positive");
        }
        return value;
    }

    public static int requirePositive(int value, String name) {
        if (value <= 0) {
            throw new DomainValidationException(name + " must be positive");
        }
        return value;
    }

    public static BigDecimal requireNonNegative(BigDecimal value, String name) {
        requireNotNull(value, name);
        if (value.signum() < 0) {
            throw new DomainValidationException(name + " must not be negative");
        }
        return value;
    }

    public static int requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new DomainValidationException(name + " must not be negative");
        }
        return value;
    }

}
