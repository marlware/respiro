import Foundation

/// Subscribes to the backend's `/api/vitals/stream` Server-Sent Events
/// endpoint and yields a decoded `VitalsReading` for each `data:` line.
struct VitalsEventStream {
    let baseURL: URL
    private let session: URLSession

    init(baseURL: URL, session: URLSession = .shared) {
        self.baseURL = baseURL
        self.session = session
    }

    func readings() -> AsyncThrowingStream<VitalsReading, Error> {
        AsyncThrowingStream { continuation in
            let task = Task {
                do {
                    let url = baseURL.appendingPathComponent("/api/vitals/stream")
                    let (bytes, response) = try await session.bytes(for: URLRequest(url: url))
                    guard let http = response as? HTTPURLResponse, http.statusCode == 200 else {
                        continuation.finish(throwing: RespiroAPIError.unexpectedResponse)
                        return
                    }

                    let decoder = JSONDecoder()
                    decoder.dateDecodingStrategy = .iso8601

                    for try await line in bytes.lines {
                        guard let payload = line.dropPrefix("data:") else { continue }
                        let data = Data(payload.trimmingCharacters(in: .whitespaces).utf8)
                        guard !data.isEmpty else { continue }
                        let reading = try decoder.decode(VitalsReading.self, from: data)
                        continuation.yield(reading)
                    }
                    continuation.finish()
                } catch {
                    continuation.finish(throwing: error)
                }
            }

            continuation.onTermination = { _ in task.cancel() }
        }
    }
}

private extension String {
    func dropPrefix(_ prefix: String) -> String? {
        guard hasPrefix(prefix) else { return nil }
        return String(dropFirst(prefix.count))
    }
}
