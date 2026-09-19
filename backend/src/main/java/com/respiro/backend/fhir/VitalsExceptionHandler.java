package com.respiro.backend.fhir;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class VitalsExceptionHandler {

    @ExceptionHandler(NoReadingsAvailableException.class)
    public ResponseEntity<Map<String, String>> handleNoReadingsAvailable(NoReadingsAvailableException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(InvalidHistoryLimitException.class)
    public ResponseEntity<Map<String, String>> handleInvalidHistoryLimit(InvalidHistoryLimitException exception) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", exception.getMessage()));
    }
}
