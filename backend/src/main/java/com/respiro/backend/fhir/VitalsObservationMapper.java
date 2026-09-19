package com.respiro.backend.fhir;

import com.respiro.backend.telemetry.VitalsReading;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Maps a simulated reading onto the two LOINC-coded Observations a pulse
 * oximeter reports: oxygen saturation and pulse rate.
 */
@Component
public class VitalsObservationMapper {

    private static final String LOINC_SYSTEM = "http://loinc.org";
    private static final String SPO2_CODE = "59408-5";
    private static final String SPO2_DISPLAY = "Oxygen saturation in Arterial blood by Pulse oximetry";
    private static final String PULSE_CODE = "8867-4";
    private static final String PULSE_DISPLAY = "Heart rate";

    public List<Observation> toObservations(VitalsReading reading) {
        Date recordedAt = Date.from(reading.recordedAt());
        return List.of(
                buildObservation(SPO2_CODE, SPO2_DISPLAY, reading.spo2Percent(), "%", recordedAt),
                buildObservation(PULSE_CODE, PULSE_DISPLAY, reading.pulseBpm(), "/min", recordedAt)
        );
    }

    private Observation buildObservation(String code, String display, double value, String unit, Date recordedAt) {
        Observation observation = new Observation();
        observation.setStatus(Observation.ObservationStatus.FINAL);
        observation.setCode(new CodeableConcept().addCoding(
                new Coding(LOINC_SYSTEM, code, display)));
        observation.setEffective(new DateTimeType(recordedAt));
        observation.setValue(new Quantity()
                .setValue(value)
                .setUnit(unit)
                .setSystem("http://unitsofmeasure.org")
                .setCode(unit));
        return observation;
    }
}
