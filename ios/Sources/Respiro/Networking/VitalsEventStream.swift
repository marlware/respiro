import Foundation

/// Subscribes to the backend's `/api/vitals/stream` Server-Sent Events
/// endpoint and yields a decoded `VitalsReading` for each `data:` line.
struct VitalsEventStream {
    let baseURL: URL
    private let apiKey: String?
    private let session: URLSession

    /// `apiKey` should be `nil` unless the backend was started with
    /// `app.api-key` set, in which case it must match that value.
    init(baseURL: URL, apiKey: String? = nil, session: URLSession = .shared) {
        self.baseURL = baseURL
        self.apiKey = apiKey
        self.session = session
    }

    func readings() -> AsyncThrowingStream<VitalsReading, Error> {
        AsyncThrowingStream { continuation in
            let task = Task {
                do {
                    let url = baseURL.appendingPathComponent("/api/vitals/stream")
                    var request = URLRequest(url: url)
                    if let apiKey {
                        request.setValue(apiKey, forHTTPHeaderField: "X-API-Key")
                    }
                    let (bytes, response) = try await session.bytes(for: request)
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
