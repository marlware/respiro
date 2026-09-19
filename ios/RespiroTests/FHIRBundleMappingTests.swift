import XCTest
@testable import Respiro

final class FHIRBundleMappingTests: XCTestCase {

    func testPairsSpo2AndPulseObservationsSharingATimestamp() {
        let timestamp = Date(timeIntervalSince1970: 1_700_000_000)
        let bundle = FHIRBundle(entry: [
            observationEntry(loincCode: LOINCCode.oxygenSaturation, value: 97, at: timestamp),
            observationEntry(loincCode: LOINCCode.heartRate, value: 72, at: timestamp),
        ])

        let readings = bundle.toVitalsReadings()

        XCTAssertEqual(readings.count, 1)
        XCTAssertEqual(readings.first?.spo2Percent, 97)
        XCTAssertEqual(readings.first?.pulseBpm, 72)
        XCTAssertEqual(readings.first?.recordedAt, timestamp)
    }

    func testDropsIncompletePairs() {
        let bundle = FHIRBundle(entry: [
            observationEntry(loincCode: LOINCCode.oxygenSaturation, value: 97, at: Date(timeIntervalSince1970: 1_700_000_000)),
        ])

        XCTAssertTrue(bundle.toVitalsReadings().isEmpty)
    }

    func testHandlesMultipleTimestampsInOrder() {
        let earlier = Date(timeIntervalSince1970: 1_700_000_000)
        let later = Date(timeIntervalSince1970: 1_700_000_010)
        let bundle = FHIRBundle(entry: [
            observationEntry(loincCode: LOINCCode.oxygenSaturation, value: 96, at: later),
            observationEntry(loincCode: LOINCCode.heartRate, value: 80, at: later),
            observationEntry(loincCode: LOINCCode.oxygenSaturation, value: 98, at: earlier),
            observationEntry(loincCode: LOINCCode.heartRate, value: 70, at: earlier),
        ])

        let readings = bundle.toVitalsReadings()

        XCTAssertEqual(readings.map(\.recordedAt), [earlier, later])
    }

    private func observationEntry(loincCode: String, value: Double, at date: Date) -> FHIREntry {
        FHIREntry(resource: FHIRObservation(
            code: FHIRCodeableConcept(coding: [FHIRCoding(system: "http://loinc.org", code: loincCode, display: nil)]),
            effectiveDateTime: date,
            valueQuantity: FHIRQuantity(value: value, unit: nil)
        ))
    }
}
