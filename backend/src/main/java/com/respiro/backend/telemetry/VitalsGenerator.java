package com.respiro.backend.telemetry;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Stands in for a device continuously reporting readings. Runs on a fixed
 * cadence so the dashboard and graph have a steady stream to poll or
 * subscribe to, rather than only producing a reading on request.
 */
@Component
public class VitalsGenerator {

    private final SimulatedVitalsSource vitalsSource;
    private final VitalsReadingRepository repository;
    private final VitalsBroadcaster broadcaster;

    public VitalsGenerator(SimulatedVitalsSource vitalsSource,
                            VitalsReadingRepository repository,
                            VitalsBroadcaster broadcaster) {
        this.vitalsSource = vitalsSource;
        this.repository = repository;
        this.broadcaster = broadcaster;
    }

    @Scheduled(fixedRate = 3000)
    public void generateReading() {
        VitalsReading reading = vitalsSource.nextReading();
        repository.save(new VitalsReadingEntity(reading.spo2Percent(), reading.pulseBpm(), reading.recordedAt()));
        broadcaster.publish(reading);
    }
}
