import Foundation

@MainActor
final class VitalsViewModel: ObservableObject {
    @Published private(set) var latest: VitalsReading?
    @Published private(set) var history: [VitalsReading] = []
    @Published private(set) var isConnected = false

    private let apiClient: RespiroAPIClient
    private let eventStream: VitalsEventStream
    private var streamTask: Task<Void, Never>?

    private let maxHistory = 50

    init(baseURL: URL) {
        self.apiClient = RespiroAPIClient(baseURL: baseURL)
        self.eventStream = VitalsEventStream(baseURL: baseURL)
    }

    func start() {
        guard streamTask == nil else { return }

        Task {
            history = (try? await apiClient.fetchHistory()) ?? []
            latest = history.last
        }

        streamTask = Task {
            while !Task.isCancelled {
                do {
                    for try await reading in eventStream.readings() {
                        isConnected = true
                        record(reading)
                    }
                } catch {
                    isConnected = false
                }
                if !Task.isCancelled {
                    try? await Task.sleep(for: .seconds(2))
                }
            }
        }
    }

    func stop() {
        streamTask?.cancel()
        streamTask = nil
        isConnected = false
    }

    private func record(_ reading: VitalsReading) {
        latest = reading
        history.append(reading)
        if history.count > maxHistory {
            history.removeFirst(history.count - maxHistory)
        }
    }
}
