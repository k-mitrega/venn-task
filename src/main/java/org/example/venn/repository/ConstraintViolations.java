package org.example.venn.repository;

import org.hibernate.exception.ConstraintViolationException;

final class ConstraintViolations {

    private ConstraintViolations() {
    }

    static boolean isUniqueViolation(Throwable failure, String constraintName) {
        for (Throwable t = failure; t != null; t = t.getCause()) {
            if (t instanceof ConstraintViolationException violation) {
                final var reportedName = violation.getConstraintName();
                return reportedName == null || reportedName.toUpperCase().contains(constraintName);
            }
        }
        return false;
    }
}
