# Respiro

Respiro is an accessible iOS app that turns standardized FHIR pulmonary data into a simple, real-time oxygen and pulse monitoring experience.

### Why

Home pulse oximeter data is often trapped inside consumer apps, while healthcare systems rely on standards like HL7 FHIR.

Respiro focuses on one small problem: making SpO2 and pulse data easy to view on iPhone while keeping the underlying data in a healthcare-standard format.

The goal of Respiro to make pulmonary telemetry more accessible, interoperable, and easier to understand.

## System architecture

### Tech stack

- Swift
  - SwiftUI for the app interface
  - UIKit for the specialized, real-time graph
- Java, Spring Boot, REST APIs  
- HL7 FHIR, HAPI FHIR  
- JUnit, XCTest  
- Docker, Amazon ECR, Amazon ECS, AWS IAM  
- Git, GitHub, GitHub Actions

### Diagram

```mermaid
flowchart TD
    A[🫁 Simulated SpO2 + Pulse Data]
    B[☕ Spring Boot Backend]
    C[🏥 HAPI FHIR Observation]
    D[🔌 REST API]
    E[📱 Swift iOS Client]
    F[♿ SwiftUI Dashboard]
    G[📈 UIKit Telemetry Graph]

    A --> B
    B --> C
    C --> D
    D --> E
    E --> F
    E --> G
```

## Getting started

### Backend

```
cd backend
mvn spring-boot:run
```

Starts on `http://localhost:8080` and begins generating a simulated reading every 3 seconds.

- `GET /api/vitals/latest` — most recent reading, as a FHIR `Bundle` of `Observation`s
- `GET /api/vitals/history?limit=50` — recent readings (1–50), same shape
- `GET /api/vitals/stream` — Server-Sent Events stream of new readings as they're generated
- `GET /actuator/health` — health check

Run the tests with `mvn test`, or build the container with `docker build -t respiro-backend backend`.

By default the API is open and any origin can call it — fine for local development. Set `app.api-key` (or the `APP_API_KEY` env var) to require an `X-API-Key` header on `/api/**` requests, and `app.cors.allowed-origins` to restrict which origins can call it from a browser.

### iOS client

The models, networking, view model, and views live in `ios/Sources/Respiro/` as a Swift package (`ios/Package.swift`), with tests in `ios/Tests/RespiroTests/`. This part doesn't need Xcode:

```
cd ios
swift test
```

The app entry point (`ios/Respiro/App/RespiroApp.swift`) does need Xcode on macOS, since it's what turns this into an actual installable app:

1. Create a new iOS App project in Xcode (SwiftUI interface).
2. Add `ios/` as a local Swift package dependency (File → Add Package Dependencies → Add Local), and add `Respiro` to the app target.
3. Add `ios/Respiro/App/RespiroApp.swift` to the app target, in place of the default one Xcode generates.
4. If running on a physical device, update `RespiroApp.backendURL` to your Mac's LAN address — `localhost` on a device refers to the device itself.
5. If the backend has `app.api-key` set, pass it to `VitalsViewModel(baseURL:apiKey:)` in `RespiroApp`.
6. Run the backend first, then build and run the app.
