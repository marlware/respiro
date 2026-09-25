package com.respiro.backend.telemetry;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VitalsServiceTest {

    private final VitalsReadingRepository repository = mock(VitalsReadingRepository.class);
    private final VitalsService service = new VitalsService(repository);

    @Test
    void latestIsEmptyWhenNothingStored() {
        when(repository.findFirstByOrderByRecordedAtDesc()).thenReturn(null);

        assertTrue(service.latest().isEmpty());
    }

    @Test
    void latestMapsStoredEntityToReading() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        when(repository.findFirstByOrderByRecordedAtDesc())
                .thenReturn(new VitalsReadingEntity(97, 72, now));

        assertEquals(new VitalsReading(97, 72, now), service.latest().orElseThrow());
    }

    @Test
    void recentHistoryPreservesRepositoryOrder() {
        Instant newer = Instant.parse("2026-01-01T00:00:03Z");
        Instant older = Instant.parse("2026-01-01T00:00:00Z");
        when(repository.findTop50ByOrderByRecordedAtDesc()).thenReturn(List.of(
                new VitalsReadingEntity(96, 80, newer),
                new VitalsReadingEntity(98, 70, older)));

        assertEquals(List.of(
                new VitalsReading(96, 80, newer),
                new VitalsReading(98, 70, older)), service.recentHistory());
    }
}
