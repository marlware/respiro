import SwiftUI

@main
struct RespiroApp: App {
    /// Points at the Spring Boot backend. Swap for your machine's LAN
    /// address when running on a physical device, since "localhost" there
    /// refers to the device itself, not your development machine.
    private static let backendURL = URL(string: "http://localhost:8080")!

    @StateObject private var viewModel = VitalsViewModel(baseURL: backendURL)

    var body: some Scene {
        WindowGroup {
            DashboardView(viewModel: viewModel)
        }
    }
}
