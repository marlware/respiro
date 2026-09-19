import SwiftUI

public struct DashboardView: View {
    @ObservedObject var viewModel: VitalsViewModel

    public init(viewModel: VitalsViewModel) {
        self.viewModel = viewModel
    }

    public var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                connectionStatus

                if let latest = viewModel.latest {
                    VStack(spacing: 20) {
                        VitalReadoutView(
                            label: "Oxygen saturation",
                            value: "\(latest.spo2Percent)",
                            unit: "%",
                            isAlarming: latest.isSpo2Low
                        )
                        VitalReadoutView(
                            label: "Pulse",
                            value: "\(latest.pulseBpm)",
                            unit: "BPM",
                            isAlarming: false
                        )
                    }

                    TelemetryGraphView(history: viewModel.history)
                        .frame(height: 180)
                        .accessibilityLabel("Pulse and oxygen saturation over time")
                } else {
                    ProgressView("Waiting for readings…")
                        .padding(.top, 40)
                }
            }
            .padding()
        }
        .onAppear { viewModel.start() }
        .onDisappear { viewModel.stop() }
    }

    private var connectionStatus: some View {
        HStack(spacing: 6) {
            Circle()
                .fill(viewModel.isConnected ? Color.green : Color.secondary)
                .frame(width: 8, height: 8)
            Text(viewModel.isConnected ? "Live" : "Reconnecting…")
                .font(.footnote)
                .foregroundStyle(.secondary)
        }
        .accessibilityElement(children: .combine)
    }
}

private struct VitalReadoutView: View {
    let label: String
    let value: String
    let unit: String
    let isAlarming: Bool

    var body: some View {
        VStack(spacing: 4) {
            Text(label)
                .font(.headline)
                .foregroundStyle(.secondary)

            HStack(alignment: .firstTextBaseline, spacing: 4) {
                Text(value)
                    .font(.system(size: 56, weight: .bold, design: .rounded))
                    .monospacedDigit()
                Text(unit)
                    .font(.title3)
                    .foregroundStyle(.secondary)

                if isAlarming {
                    Image(systemName: "exclamationmark.triangle.fill")
                        .foregroundStyle(.red)
                        .accessibilityLabel("Low")
                }
            }
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 16))
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(label): \(value) \(unit)\(isAlarming ? ", low" : "")")
    }
}
