import Foundation

/// A single SpO2/pulse reading, decoded from the plain JSON the live
/// stream sends (mirrors the backend's `VitalsReading` record) or built
/// from a parsed FHIR `Observation` pair.
struct VitalsReading: Codable, Identifiable, Equatable {
    var id: Date { recordedAt }

    let spo2Percent: Int
    let pulseBpm: Int
    let recordedAt: Date

    private enum CodingKeys: String, CodingKey {
        case spo2Percent, pulseBpm, recordedAt
    }
}

extension VitalsReading {
    /// Whether SpO2 has dropped low enough to warrant flagging in the UI.
    /// 90% is the commonly used threshold for clinically low oxygen saturation.
    var isSpo2Low: Bool { spo2Percent < 90 }
}
