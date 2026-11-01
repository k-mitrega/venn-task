package org.example.venn.service;

public class DuplicateLoadConflictException extends RuntimeException {

    public DuplicateLoadConflictException() {
        super("load attempt already exists for customer and load id");
    }

    public DuplicateLoadConflictException(Throwable cause) {
        super("load attempt already exists for customer and load id", cause);
    }
}
