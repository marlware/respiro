package com.respiro.backend.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.respiro.backend.telemetry.SimulatedVitalsSource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VitalsController {

    private static final String FHIR_JSON = "application/fhir+json";

    private final SimulatedVitalsSource vitalsSource;
    private final VitalsObservationMapper observationMapper;
    private final IParser fhirJsonParser;

    public VitalsController(SimulatedVitalsSource vitalsSource,
                             VitalsObservationMapper observationMapper,
                             FhirContext fhirContext) {
        this.vitalsSource = vitalsSource;
        this.observationMapper = observationMapper;
        this.fhirJsonParser = fhirContext.newJsonParser().setPrettyPrint(true);
    }

    @GetMapping(value = "/api/vitals/latest", produces = FHIR_JSON)
    public String latest() {
        Bundle bundle = new Bundle().setType(Bundle.BundleType.COLLECTION);
        for (Observation observation : observationMapper.toObservations(vitalsSource.nextReading())) {
            bundle.addEntry().setResource(observation);
        }
        return fhirJsonParser.encodeResourceToString(bundle);
    }
}
