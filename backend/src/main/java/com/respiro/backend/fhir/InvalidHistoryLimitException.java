package com.respiro.backend.fhir;

public class InvalidHistoryLimitException extends RuntimeException {

    public InvalidHistoryLimitException(int limit) {
        super("limit must be between 1 and 50, was " + limit);
    }
}
