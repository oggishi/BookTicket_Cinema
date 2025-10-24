# BookTicket_Cinema

Professional cinema ticket booking system — a production-ready Java application for managing movies, showtimes, seat maps, bookings, payments, and reporting. This single README contains both a high-level professional overview and the detailed Functions & Tasks backlog to guide development and contribution.

[![Language](https://img.shields.io/badge/Language-Java-blue)]()
[![Status](https://img.shields.io/badge/Status-Active-green)]()
[![License](https://img.shields.io/badge/License-MIT-lightgrey)]()

---

## Professional overview

BookTicket_Cinema centralizes cinema operations: movie scheduling, seat allocation, booking lifecycle, payments, ticket issuance, and reporting. It's engineered for high-concurrency booking environments with a focus on transactional safety, idempotency, and observability. The codebase is organized to separate domain logic from adapters (web, db, payments) for easy testing and replacement.

Intended audiences:
- Cinema operators and box-office teams
- Backend engineers integrating ticketing into existing systems
- DevOps teams operating scalable booking infrastructure
- Developers extending payment gateways, frontends, or reporting

Core goals:
- Reliable seat allocation under concurrent load
- Clear, testable business logic for bookings and pricing
- Pluggable payment adapters and auditable ticket issuance
- Developer-friendly scaffolding for fast iteration and CI

---

## Key features

- Movie catalog with metadata (title, rating, runtime, languages, poster)
- Multi-location support (cinemas, screens, seat maps)
- Showtimes scheduling with conflict detection and blackout rules
- Real-time seat availability, holds, and timed releases
- Booking lifecycle: hold → reserve → pay → issue ticket (QR/barcode)
- Payment integrations (pluggable adapters), idempotency keys, webhook safety
- Cancellations & refunds with configurable policies
- Role-based access control (Admin, BoxOffice, Customer)
- Reporting (sales, occupancy, refunds) and export (CSV/JSON/PDF)
- Notifications: email/SMS receipts and reminders
- Auditing and activity logs for compliance and troubleshooting
- Tests, CI-friendly configuration, and Docker examples for local dev

---

## Technology & recommended stack

- Language: Java (100% of the repository)
- JVM: Java 11+ (Java 17 recommended)
- Framework: Spring Boot + Spring Data JPA (recommended)
- Build: Maven or Gradle
- DB: PostgreSQL (production), H2 (dev/tests)
- Testing: JUnit, Mockito, Testcontainers (integration)
- Optional: Liquibase/Flyway (migrations), Lombok, Prometheus/Grafana for observability

---

## System requirements

- JDK 11+ (17 recommended)
- Maven 3.6+ or Gradle 6+
- PostgreSQL for production environments
- Docker & Docker Compose (recommended for local stacks)

---

## Quick start — build & run

With Maven:
```bash
mvn clean verify
mvn -DskipTests=false package
java -jar target/bookticket-cinema-<version>.jar
```

With Gradle:
```bash
./gradlew build
./gradlew bootRun
```

Docker (example):
```bash
docker-compose up --build
```

Configuration:
- Edit src/main/resources/application.yml (or application.properties) to set DB URL, credentials, server port and payment provider.
- Provide a .env.example and application-example.yml for onboarding.

---

## Booking & concurrency notes (important)

- Protect booking operations in DB transactions; use SELECT ... FOR UPDATE or optimistic locking to avoid double-booking.
- Use idempotency keys for booking creation and payment webhooks to handle retries safely.
- Implement a short hold window for unpaid bookings and a scheduled job to release expired holds.
- Prefer Testcontainers for integration tests that validate transactional behavior and concurrency scenarios.

---

## Functions & Tasks (combined — use this as the implementation backlog)

This section defines the core functions and a prioritized set of implementation tasks (P0 = must have, P1 = important, P2 = nice-to-have). Each function includes acceptance criteria and testing guidance so tasks can be converted directly into issues.

### Core functions

1. Movie Catalog
   - Purpose: Manage movie metadata and search.
   - Acceptance:
     - CRUD endpoints with validation
     - Search by title/genre/language
     - Poster handling (upload or URL)
   - Test guidance: controller/service unit tests; integration test for upload handling

2. Cinemas, Screens & Seat Maps
   - Purpose: Model cinemas, screens, and seat layout with seat types.
   - Acceptance:
     - Seat maps represent coordinates and types (regular/VIP/accessible)
     - Admin preview API/UI for layouts
   - Test guidance: unit tests for seat map serialization; integration for preview

3. Showtimes & Scheduling
   - Purpose: Create showtimes with conflict detection and recurring rules.
   - Acceptance:
     - Prevent overlapping shows on same screen
     - Support recurring scheduling and blackout windows
   - Test guidance: unit tests for scheduling rules

4. Real-time Seat Availability & Holds
   - Purpose: Allow customers to reserve seats temporarily while completing payment.
   - Acceptance:
     - Holds created with expiration; expired holds free seats
     - Holds are concurrency-safe (no double-hold)
   - Test guidance: concurrency tests; integration test with in-memory DB or Testcontainers

5. Booking Lifecycle & Payments
   - Purpose: Orchestrate hold → confirm → payment → ticket issuance.
   - Acceptance:
     - Booking API requires idempotency key
     - Payment adapter abstraction; mockable provider for tests
     - Transactional integrity: payment success results in booked seats and ticket issuance; failures release holds
   - Test guidance: simulate payment retries, failures, and idempotent replays

6. Ticketing (QR / Barcode)
   - Purpose: Generate verifiable ticket tokens & printable tickets.
   - Acceptance:
     - Unique, verifiable token per ticket
     - Tickets include seat, showtime, and cinema info
   - Test guidance: unit tests for token generation and validation

7. Cancellations & Refunds
   - Purpose: Support policy-driven cancellation and refund flows.
   - Acceptance:
     - Refunds via payment provider adapter; partial refunds supported
     - Audit trail for refunds
   - Test guidance: integration for refund flows with mocked providers

8. User Accounts & Profiles
   - Purpose: Support customer accounts, booking history, tokenized payment methods.
   - Acceptance:
     - OAuth optional; store tokenized payment references, not raw card data
   - Test guidance: auth integration tests, profile endpoints

9. Roles & Access Control (RBAC)
   - Purpose: Enforce Admin, BoxOffice, Customer permissions.
   - Acceptance:
     - Only authorized roles perform restricted actions (create showtimes, issue refunds)
   - Test guidance: security integration tests

10. Reporting & Exports
    - Purpose: Provide sales, occupancy, and refund reports (CSV/JSON/PDF).
    - Acceptance:
      - Export endpoints produce correct aggregates; filters respected
    - Test guidance: report correctness tests against seeded data

11. Notifications
    - Purpose: Email/SMS confirmations and reminders.
    - Acceptance:
      - Configurable templates; mockable channel adapters for tests
    - Test guidance: unit tests; integration tests with mocked providers

12. Background Tasks & Scheduling
    - Purpose: Release expired holds, nightly reconciliation, analytics aggregation.
    - Acceptance:
      - Jobs are idempotent, observable, and have health checks
    - Test guidance: job integration tests

13. Auditing & Activity Logs
    - Purpose: Track critical operations (who/what/when).
    - Acceptance:
      - Audit entries created for create/update/delete on booking/payment actions
    - Test guidance: audit verification in integration tests

---

### Suggested data model (high level)
- Movie { id, title, synopsis, runtimeMins, rating, genres[], languages[], posterUrl, createdAt }
- Cinema { id, name, address, timezone }
- Screen { id, cinemaId, name, seatMapId }
- SeatMap { id, rows, seats[{seatId, row, number, type}] }
- Showtime { id, screenId, movieId, startAt, endAt, priceTierId, status }
- Hold { id, showtimeId, seatIds[], userId?, expiresAt, createdAt }
- Booking { id, showtimeId, seatIds[], userId, status, amount, paymentId, createdAt, updatedAt }
- Payment { id, provider, providerRef, status, amount, currency, capturedAt }
- User { id, email, name, phone, roles[], createdAt }
- Audit { id, entityType, entityId, action, actorUserId, timestamp, details }

---

### Prioritized implementation tasks (backlog)

P0 — Core (must have)
- Initialize project structure (Maven/Gradle), package layout
  - Tests: build passes
- Movie, Cinema, Screen, SeatMap entities + CRUD APIs
  - Tests: controller/service tests
- Showtime scheduling with conflict detection
  - Tests: unit tests cover conflict scenarios
- Seat holds + automatic expiry worker
  - Tests: concurrency and expiry tests
- Booking API with idempotency and payment adapter (mocked)
  - Tests: simulate retries and payment failures

P1 — Important
- Integrate a payment provider adapter (pluggable)
  - Tests: webhook idempotency & signature verification
- Ticket generation (QR/barcode) and verification endpoint
  - Tests: token verification tests
- RBAC: Admin, BoxOffice, Customer roles
  - Tests: security integration tests
- Dockerfile + docker-compose for local dev
  - Tests: compose brings up app + Postgres

P2 — Enhancements
- Reporting endpoints & exports (CSV/PDF)
- Refund/cancellation automation & policy engine
- CI pipeline with Testcontainers for Postgres integration tests
- Observability: Prometheus metrics + Grafana dashboards
- Mobile-friendly API examples and sample frontend

---

## Example API sketches (for quick implementation)

- GET /api/movies
- GET /api/movies/{id}
- POST /api/movies
- GET /api/showtimes?cinemaId=&date=
- POST /api/showtimes/{id}/holds  -> create hold (expiresAt)
- POST /api/bookings  -> body includes holdId or seatIds; header X-Idempotency-Key
- GET /api/bookings/{id}
- POST /api/bookings/{id}/cancel
- GET /api/reports/sales?start=&end=

---

## Contributing

How to contribute:
1. Fork and create a feature branch: git checkout -b feat/your-feature
2. Add tests for new behavior
3. Open a PR with clear description and acceptance criteria
4. Follow code style, add migrations for DB changes, and update README

Add CONTRIBUTING.md and CODE_OF_CONDUCT.md to the repo to document expectations.

---

## Troubleshooting & FAQ

- Double-booking errors: check transaction boundaries and locking strategy
- Webhook failures: validate signatures and implement idempotency
- Tests failing locally: verify Java version and that Testcontainers (if used) can start Docker

---

## License & maintainers

- License: MIT (replace if different)
- Maintainer: oggishi (https://github.com/oggishi)
- Contact: add an email or link in the repo

---

Thank you for reviewing BookTicket_Cinema. This single README combines the professional project overview with a concrete Functions & Tasks backlog you can use to create issues, prioritize work, or scaffold the initial codebase.
## Tài khoản admin :
Email: admin@gmail.com

password: 1

## Tài khoản user 
Email: ndtphuongha@gmail.com

password: trung1






## Database
https://console.firebase.google.com/project/cinematicketbooking-60e22/firestore/databases/-default-/data/~2FMovie~2FXyoudfQzNifRzKIQJnEb







