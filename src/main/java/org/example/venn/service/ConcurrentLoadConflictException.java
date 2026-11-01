package org.example.venn.service;

public class ConcurrentLoadConflictException extends RuntimeException {

    public ConcurrentLoadConflictException(Throwable cause) {
        super("usage for this customer was concurrently created by another request", cause);
    }
}
