import XCTest
@testable import Respiro

final class VitalsReadingTests: XCTestCase {

    func testFlagsSpo2BelowNinetyAsLow() {
        let reading = VitalsReading(spo2Percent: 89, pulseBpm: 72, recordedAt: Date())
        XCTAssertTrue(reading.isSpo2Low)
    }

    func testDoesNotFlagSpo2AtOrAboveNinety() {
        let reading = VitalsReading(spo2Percent: 90, pulseBpm: 72, recordedAt: Date())
        XCTAssertFalse(reading.isSpo2Low)
    }
}
