package com.respiro.backend.telemetry;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Stands in for a real pulse oximeter feed. Readings drift gently around a
 * healthy baseline rather than jumping randomly, so the client graph looks
 * like a real waveform instead of noise.
 */
@Component
public class SimulatedVitalsSource {

    private static final int BASELINE_SPO2 = 97;
    private static final int BASELINE_PULSE = 72;

    private int spo2 = BASELINE_SPO2;
    private int pulse = BASELINE_PULSE;

    public synchronized VitalsReading nextReading() {
        spo2 = drift(spo2, BASELINE_SPO2, 90, 100);
        pulse = drift(pulse, BASELINE_PULSE, 55, 110);
        return new VitalsReading(spo2, pulse, Instant.now());
    }

    private int drift(int current, int baseline, int min, int max) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int step = random.nextInt(-2, 3);
        int pulledTowardBaseline = current + step + Integer.signum(baseline - current);
        return Math.max(min, Math.min(max, pulledTowardBaseline));
    }
}
