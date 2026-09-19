import Foundation

enum RespiroAPIError: Error {
    case unexpectedResponse
    case decoding(Error)
}

/// Talks to the Spring Boot backend's `/api/vitals` endpoints.
struct RespiroAPIClient {
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

    func fetchLatest() async throws -> VitalsReading {
        let bundle = try await fetchBundle(path: "/api/vitals/latest")
        guard let reading = bundle.toVitalsReadings().first else {
            throw RespiroAPIError.unexpectedResponse
        }
        return reading
    }

    func fetchHistory(limit: Int = 50) async throws -> [VitalsReading] {
        let bundle = try await fetchBundle(path: "/api/vitals/history", query: [URLQueryItem(name: "limit", value: "\(limit)")])
        return bundle.toVitalsReadings()
    }

    private func fetchBundle(path: String, query: [URLQueryItem] = []) async throws -> FHIRBundle {
        var components = URLComponents(url: baseURL.appendingPathComponent(path), resolvingAgainstBaseURL: false)
        components?.queryItems = query.isEmpty ? nil : query
        guard let url = components?.url else { throw RespiroAPIError.unexpectedResponse }

        var request = URLRequest(url: url)
        request.setValue("application/fhir+json", forHTTPHeaderField: "Accept")
        if let apiKey {
            request.setValue(apiKey, forHTTPHeaderField: "X-API-Key")
        }

        let (data, response) = try await session.data(for: request)
        guard let http = response as? HTTPURLResponse, http.statusCode == 200 else {
            throw RespiroAPIError.unexpectedResponse
        }

        do {
            let decoder = JSONDecoder()
            decoder.dateDecodingStrategy = .iso8601
            return try decoder.decode(FHIRBundle.self, from: data)
        } catch {
            throw RespiroAPIError.decoding(error)
        }
    }
}
