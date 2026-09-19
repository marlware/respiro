package com.respiro.backend.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.respiro.backend.telemetry.VitalsBroadcaster;
import com.respiro.backend.telemetry.VitalsReading;
import com.respiro.backend.telemetry.VitalsService;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
public class VitalsController {

    private static final String FHIR_JSON = "application/fhir+json";

    private final VitalsService vitalsService;
    private final VitalsBroadcaster broadcaster;
    private final VitalsObservationMapper observationMapper;
    private final IParser fhirJsonParser;

    public VitalsController(VitalsService vitalsService,
                             VitalsBroadcaster broadcaster,
                             VitalsObservationMapper observationMapper,
                             FhirContext fhirContext) {
        this.vitalsService = vitalsService;
        this.broadcaster = broadcaster;
        this.observationMapper = observationMapper;
        this.fhirJsonParser = fhirContext.newJsonParser().setPrettyPrint(true);
    }

    @GetMapping(value = "/api/vitals/latest", produces = FHIR_JSON)
    public String latest() {
        VitalsReading reading = vitalsService.latest()
                .orElseThrow(NoReadingsAvailableException::new);
        return toBundle(List.of(reading));
    }

    @GetMapping(value = "/api/vitals/history", produces = FHIR_JSON)
    public String history(@RequestParam(defaultValue = "50") int limit) {
        if (limit < 1 || limit > 50) {
            throw new InvalidHistoryLimitException(limit);
        }
        List<VitalsReading> readings = vitalsService.recentHistory().stream()
                .limit(limit)
                .toList();
        return toBundle(readings);
    }

    @GetMapping(value = "/api/vitals/stream", produces = "text/event-stream")
    public SseEmitter stream() {
        return broadcaster.subscribe();
    }

    private String toBundle(List<VitalsReading> readings) {
        Bundle bundle = new Bundle().setType(Bundle.BundleType.COLLECTION);
        for (VitalsReading reading : readings) {
            for (Observation observation : observationMapper.toObservations(reading)) {
                bundle.addEntry().setResource(observation);
            }
        }
        return fhirJsonParser.encodeResourceToString(bundle);
    }
}
