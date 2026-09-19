package com.respiro.backend.telemetry;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class VitalsReadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int spo2Percent;
    private int pulseBpm;
    private Instant recordedAt;

    protected VitalsReadingEntity() {
        // required by JPA
    }

    public VitalsReadingEntity(int spo2Percent, int pulseBpm, Instant recordedAt) {
        this.spo2Percent = spo2Percent;
        this.pulseBpm = pulseBpm;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public int getSpo2Percent() {
        return spo2Percent;
    }

    public int getPulseBpm() {
        return pulseBpm;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public VitalsReading toReading() {
        return new VitalsReading(spo2Percent, pulseBpm, recordedAt);
    }
}
