import Foundation

/// A single SpO2/pulse reading, decoded from the plain JSON the live
/// stream sends (mirrors the backend's `VitalsReading` record) or built
/// from a parsed FHIR `Observation` pair.
public struct VitalsReading: Codable, Identifiable, Equatable {
    public var id: Date { recordedAt }

    public let spo2Percent: Int
    public let pulseBpm: Int
    public let recordedAt: Date

    private enum CodingKeys: String, CodingKey {
        case spo2Percent, pulseBpm, recordedAt
    }

    public init(spo2Percent: Int, pulseBpm: Int, recordedAt: Date) {
        self.spo2Percent = spo2Percent
        self.pulseBpm = pulseBpm
        self.recordedAt = recordedAt
    }
}

public extension VitalsReading {
    /// Whether SpO2 has dropped low enough to warrant flagging in the UI.
    /// 90% is the commonly used threshold for clinically low oxygen saturation.
    var isSpo2Low: Bool { spo2Percent < 90 }
}
