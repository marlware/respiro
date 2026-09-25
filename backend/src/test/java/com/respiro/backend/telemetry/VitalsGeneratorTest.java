package com.respiro.backend.telemetry;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class VitalsGeneratorTest {

    private static final VitalsReading READING = new VitalsReading(96, 74, Instant.parse("2026-01-01T00:00:00Z"));

    private final VitalsReadingRepository repository = mock(VitalsReadingRepository.class);
    private final List<VitalsReading> published = new ArrayList<>();

    private final SimulatedVitalsSource source = new SimulatedVitalsSource() {
        @Override
        public VitalsReading nextReading() {
            return READING;
        }
    };

    private final VitalsBroadcaster broadcaster = new VitalsBroadcaster() {
        @Override
        public void publish(VitalsReading reading) {
            published.add(reading);
        }
    };

    private final VitalsGenerator generator = new VitalsGenerator(source, repository, broadcaster);

    @Test
    void persistsAndBroadcastsEachGeneratedReading() {
        generator.generateReading();

        ArgumentCaptor<VitalsReadingEntity> saved = ArgumentCaptor.forClass(VitalsReadingEntity.class);
        verify(repository).save(saved.capture());
        assertEquals(READING, saved.getValue().toReading());
        assertEquals(List.of(READING), published);
    }
}
