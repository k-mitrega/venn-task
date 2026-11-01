package org.example.venn.repository;

public class DuplicateLoadAttemptException extends RuntimeException {

    public DuplicateLoadAttemptException(Throwable cause) {
        super("load attempt already exists for customer and load id", cause);
    }
}
