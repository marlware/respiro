package com.respiro.backend.fhir;

/**
 * Thrown when a client asks for the latest reading before the scheduled
 * generator has produced one yet (only possible in the first few seconds
 * after startup).
 */
public class NoReadingsAvailableException extends RuntimeException {

    public NoReadingsAvailableException() {
        super("No vitals readings have been recorded yet");
    }
}
