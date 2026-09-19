// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "Respiro",
    platforms: [.iOS(.v16)],
    products: [
        .library(name: "Respiro", targets: ["Respiro"]),
    ],
    targets: [
        .target(name: "Respiro", path: "Sources/Respiro"),
        .testTarget(name: "RespiroTests", dependencies: ["Respiro"], path: "Tests/RespiroTests"),
    ]
)
