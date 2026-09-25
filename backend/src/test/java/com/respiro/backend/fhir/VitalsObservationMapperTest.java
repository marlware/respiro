package com.respiro.backend.fhir;

import com.respiro.backend.telemetry.VitalsReading;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VitalsObservationMapperTest {

    private final VitalsObservationMapper mapper = new VitalsObservationMapper();
    private final Instant recordedAt = Instant.parse("2026-01-01T12:00:00Z");

    @Test
    void producesSpo2AndPulseObservationsWithLoincCodes() {
        List<Observation> observations = mapper.toObservations(new VitalsReading(97, 72, recordedAt));

        assertEquals(2, observations.size());
        assertEquals("59408-5", observations.get(0).getCode().getCodingFirstRep().getCode());
        assertEquals("8867-4", observations.get(1).getCode().getCodingFirstRep().getCode());
    }

    @Test
    void carriesValuesAndUnits() {
        List<Observation> observations = mapper.toObservations(new VitalsReading(97, 72, recordedAt));

        Quantity spo2 = observations.get(0).getValueQuantity();
        Quantity pulse = observations.get(1).getValueQuantity();
        assertEquals(97, spo2.getValue().intValue());
        assertEquals("%", spo2.getUnit());
        assertEquals(72, pulse.getValue().intValue());
        assertEquals("/min", pulse.getUnit());
    }

    @Test
    void marksObservationsFinalAndStampsRecordedTime() {
        for (Observation observation : mapper.toObservations(new VitalsReading(97, 72, recordedAt))) {
            assertEquals(Observation.ObservationStatus.FINAL, observation.getStatus());
            assertEquals(recordedAt.toEpochMilli(), observation.getEffectiveDateTimeType().getValue().getTime());
        }
    }
}
