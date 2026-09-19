import Foundation

/// Minimal FHIR R4 shapes for the fields Respiro actually reads. Not a
/// general-purpose FHIR model — just enough to pull SpO2 and pulse values
/// out of the Observation bundles the backend returns.
struct FHIRBundle: Codable {
    let entry: [FHIREntry]?
}

struct FHIREntry: Codable {
    let resource: FHIRObservation
}

struct FHIRObservation: Codable {
    let code: FHIRCodeableConcept
    let effectiveDateTime: Date?
    let valueQuantity: FHIRQuantity?
}

struct FHIRCodeableConcept: Codable {
    let coding: [FHIRCoding]

    var loincCode: String? { coding.first?.code }
}

struct FHIRCoding: Codable {
    let system: String?
    let code: String?
    let display: String?
}

struct FHIRQuantity: Codable {
    let value: Double
    let unit: String?
}

enum LOINCCode {
    static let oxygenSaturation = "59408-5"
    static let heartRate = "8867-4"
}

extension FHIRBundle {
    /// Groups the bundle's Observations by their recorded time and pairs
    /// up the SpO2 and pulse readings that share a timestamp.
    func toVitalsReadings() -> [VitalsReading] {
        let observations = entry?.map(\.resource) ?? []
        let byTimestamp = Dictionary(grouping: observations) { $0.effectiveDateTime ?? .distantPast }

        return byTimestamp.compactMap { timestamp, observations in
            guard
                let spo2 = observations.first(where: { $0.code.loincCode == LOINCCode.oxygenSaturation })?.valueQuantity?.value,
                let pulse = observations.first(where: { $0.code.loincCode == LOINCCode.heartRate })?.valueQuantity?.value
            else { return nil }

            return VitalsReading(spo2Percent: Int(spo2), pulseBpm: Int(pulse), recordedAt: timestamp)
        }
        .sorted { $0.recordedAt < $1.recordedAt }
    }
}
