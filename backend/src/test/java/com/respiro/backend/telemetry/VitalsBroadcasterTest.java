package com.respiro.backend.telemetry;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class VitalsBroadcasterTest {

    private final VitalsBroadcaster broadcaster = new VitalsBroadcaster();
    private final VitalsReading reading = new VitalsReading(97, 72, Instant.parse("2026-01-01T00:00:00Z"));

    @Test
    void eachSubscribeReturnsANewEmitter() {
        SseEmitter first = broadcaster.subscribe();
        SseEmitter second = broadcaster.subscribe();

        assertNotNull(first);
        assertNotSame(first, second);
    }

    @Test
    void publishWithNoSubscribersDoesNothing() {
        assertDoesNotThrow(() -> broadcaster.publish(reading));
    }

    @Test
    void publishToSubscribersDoesNotThrow() {
        broadcaster.subscribe();
        broadcaster.subscribe();

        assertDoesNotThrow(() -> broadcaster.publish(reading));
    }

    @Test
    void publishSurvivesASubscriberThatAlreadyCompleted() {
        SseEmitter gone = broadcaster.subscribe();
        broadcaster.subscribe();
        gone.complete();

        assertDoesNotThrow(() -> broadcaster.publish(reading));
        assertDoesNotThrow(() -> broadcaster.publish(reading));
    }
}
