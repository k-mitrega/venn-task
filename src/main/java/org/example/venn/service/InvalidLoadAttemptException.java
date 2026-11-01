package org.example.venn.service;

public class InvalidLoadAttemptException extends IllegalArgumentException {

    public InvalidLoadAttemptException(String message) {
        super(message);
    }
}
