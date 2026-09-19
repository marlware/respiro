package com.respiro.backend.telemetry;

import java.time.Instant;

public record VitalsReading(int spo2Percent, int pulseBpm, Instant recordedAt) {
}
