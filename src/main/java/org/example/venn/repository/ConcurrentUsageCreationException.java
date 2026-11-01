package org.example.venn.repository;

public class ConcurrentUsageCreationException extends RuntimeException {

    public ConcurrentUsageCreationException(Throwable cause) {
        super("usage for the customer was concurrently created by another request", cause);
    }
}
