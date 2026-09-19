import SwiftUI
import UIKit

/// SwiftUI wrapper around a UIKit-drawn line graph. Plain SwiftUI Shapes
/// redraw the whole path on every update; a dedicated CAShapeLayer lets us
/// animate just the path as new readings arrive, which matters once this
/// is ticking every few seconds.
struct TelemetryGraphView: UIViewRepresentable {
    let history: [VitalsReading]

    func makeUIView(context: Context) -> TelemetryGraphUIView {
        TelemetryGraphUIView()
    }

    func updateUIView(_ uiView: TelemetryGraphUIView, context: Context) {
        uiView.update(with: history)
    }
}

final class TelemetryGraphUIView: UIView {
    private let pulseLayer = CAShapeLayer()
    private let spo2Layer = CAShapeLayer()

    private var history: [VitalsReading] = []

    override init(frame: CGRect) {
        super.init(frame: frame)
        configureLayers()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        configureLayers()
    }

    private func configureLayers() {
        pulseLayer.strokeColor = UIColor.systemPink.cgColor
        pulseLayer.fillColor = UIColor.clear.cgColor
        pulseLayer.lineWidth = 2
        pulseLayer.lineJoin = .round

        spo2Layer.strokeColor = UIColor.systemBlue.cgColor
        spo2Layer.fillColor = UIColor.clear.cgColor
        spo2Layer.lineWidth = 2
        spo2Layer.lineJoin = .round

        layer.addSublayer(spo2Layer)
        layer.addSublayer(pulseLayer)
    }

    func update(with history: [VitalsReading]) {
        self.history = history
        setNeedsLayout()
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        guard !history.isEmpty else {
            pulseLayer.path = nil
            spo2Layer.path = nil
            return
        }

        pulseLayer.path = path(for: history.map { Double($0.pulseBpm) }, in: bounds).cgPath
        spo2Layer.path = path(for: history.map { Double($0.spo2Percent) }, in: bounds).cgPath
    }

    private func path(for values: [Double], in rect: CGRect) -> UIBezierPath {
        let path = UIBezierPath()
        guard let min = values.min(), let max = values.max(), values.count > 1 else { return path }

        let range = Swift.max(max - min, 1)
        let stepX = rect.width / CGFloat(values.count - 1)

        for (index, value) in values.enumerated() {
            let x = CGFloat(index) * stepX
            let normalized = (value - min) / range
            let y = rect.height - (CGFloat(normalized) * rect.height)
            let point = CGPoint(x: x, y: y)
            index == 0 ? path.move(to: point) : path.addLine(to: point)
        }

        return path
    }
}
