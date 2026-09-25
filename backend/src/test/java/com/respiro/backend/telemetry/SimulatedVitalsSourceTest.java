package com.respiro.backend.telemetry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatedVitalsSourceTest {

    @Test
    void readingsStayWithinPhysiologicalBounds() {
        SimulatedVitalsSource source = new SimulatedVitalsSource();

        for (int i = 0; i < 5000; i++) {
            VitalsReading reading = source.nextReading();
            assertTrue(reading.spo2Percent() >= 90 && reading.spo2Percent() <= 100);
            assertTrue(reading.pulseBpm() >= 55 && reading.pulseBpm() <= 110);
        }
    }

    @Test
    void consecutiveReadingsDriftGently() {
        SimulatedVitalsSource source = new SimulatedVitalsSource();
        VitalsReading previous = source.nextReading();

        for (int i = 0; i < 1000; i++) {
            VitalsReading next = source.nextReading();
            assertTrue(Math.abs(next.spo2Percent() - previous.spo2Percent()) <= 3);
            assertTrue(Math.abs(next.pulseBpm() - previous.pulseBpm()) <= 3);
            previous = next;
        }
    }
}
