package com.respiro.backend.fhir;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VitalsExceptionHandlerTest {

    private final VitalsExceptionHandler handler = new VitalsExceptionHandler();

    @Test
    void noReadingsAvailableIsServiceUnavailable() {
        NoReadingsAvailableException exception = new NoReadingsAvailableException();

        ResponseEntity<Map<String, String>> response = handler.handleNoReadingsAvailable(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(Map.of("error", exception.getMessage()), response.getBody());
    }

    @Test
    void invalidHistoryLimitIsBadRequest() {
        InvalidHistoryLimitException exception = new InvalidHistoryLimitException(99);

        ResponseEntity<Map<String, String>> response = handler.handleInvalidHistoryLimit(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("error", "limit must be between 1 and 50, was 99"), response.getBody());
    }
}
