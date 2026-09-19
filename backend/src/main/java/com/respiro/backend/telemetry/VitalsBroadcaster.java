package com.respiro.backend.telemetry;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Fans out each new reading to every subscribed SSE client and drops any
 * subscriber whose connection has gone away.
 */
@Component
public class VitalsBroadcaster {

    private final List<SseEmitter> subscribers = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitter.onCompletion(() -> subscribers.remove(emitter));
        emitter.onTimeout(() -> subscribers.remove(emitter));
        emitter.onError(exception -> subscribers.remove(emitter));
        subscribers.add(emitter);
        return emitter;
    }

    public void publish(VitalsReading reading) {
        for (SseEmitter emitter : subscribers) {
            try {
                emitter.send(SseEmitter.event().name("vitals").data(reading));
            } catch (IOException exception) {
                subscribers.remove(emitter);
            }
        }
    }
}
