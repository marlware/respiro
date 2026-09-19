package com.respiro.backend.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.respiro.backend.telemetry.VitalsReadingEntity;
import com.respiro.backend.telemetry.VitalsReadingRepository;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
class VitalsControllerTest {

    @Autowired
    private VitalsController controller;

    @Autowired
    private VitalsReadingRepository repository;

    @Autowired
    private FhirContext fhirContext;

    @Test
    void latestReturnsSpo2AndPulseObservations() {
        repository.save(new VitalsReadingEntity(97, 72, Instant.now()));

        String json = controller.latest();

        IParser parser = fhirContext.newJsonParser();
        Bundle bundle = (Bundle) parser.parseResource(json);

        assertThat(bundle.getEntry()).hasSize(2);
        assertThat(bundle.getEntry())
                .extracting(entry -> ((Observation) entry.getResource()).getCode().getCodingFirstRep().getCode())
                .containsExactlyInAnyOrder("59408-5", "8867-4");
    }

    @Test
    void historyRejectsLimitOutsideValidRange() {
        assertThat(catchThrowable(() -> controller.history(0))).isInstanceOf(InvalidHistoryLimitException.class);
        assertThat(catchThrowable(() -> controller.history(51))).isInstanceOf(InvalidHistoryLimitException.class);
    }
}
