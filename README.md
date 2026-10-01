# SeatReserve

Concurrent seat reservation service built with Java 21, Spring Boot, PostgreSQL and Flyway.

## Assignment decisions
- Multi-seat reservation is **all-or-nothing**.
- Seats are locked in deterministic lexicographic order using PostgreSQL row locks.
- A successful reservation immediately confirms seats; there is no payment gateway in scope.
- Cancellation is explicit via `POST /api/v1/reservations/{id}/cancel`.
- Identity comes only from `Authorization: Bearer <user-id>`; request bodies never supply user identity.
- Money is integer paise (`long` / PostgreSQL BIGINT).
- Idempotency is unique per `(user_id, show_id, idempotency_key)` and stores a SHA-256 request fingerprint.

## Run locally
```bash
mvn spring-boot:run
```
Or:
```bash
mvn spring-boot:run
```

Start PostgreSQL:
```bash
docker compose up postgres -d
```

## Build and run
```bash
mvn clean verify
mvn package
mvn spring-boot:run
```

## APIs
### Create show
```bash
curl -X POST http://localhost:8080/api/v1/shows \
  -H 'Content-Type: application/json' \
  -d '{"name":"friday-night","seats":["A1","A2","A3","A12"],"price_paise":25000,"per_user_limit":4}'
```

### Reserve
```bash
curl -X POST http://localhost:8080/api/v1/shows/<SHOW_ID>/reserve \
  -H 'Authorization: Bearer user-123' \
  -H 'Content-Type: application/json' \
  -d '{"seats":["A12"],"idempotency_key":"req-001"}'
```

### Show state
```bash
curl http://localhost:8080/api/v1/shows/<SHOW_ID>
```

### Cancel
```bash
curl -X POST http://localhost:8080/api/v1/reservations/<RESERVATION_ID>/cancel \
  -H 'Authorization: Bearer user-123'
```

### Health / metrics
- `GET /api/v1/health/live`
- `GET /actuator/health`
- `GET /actuator/health/readiness`
- `GET /actuator/prometheus`

## Concurrency mechanism
Reservation is a single database transaction. Requested seats are sorted and selected with `PESSIMISTIC_WRITE`, which maps to PostgreSQL `FOR UPDATE`. This makes the database the atomic decision point. A request either locks all requested seats and confirms them, or the transaction rolls back with a domain 409.

## Reconciliation invariant
For every show:
`available + held + confirmed == total_seats`

Current implementation uses no active hold state; seats are either available or confirmed. The invariant therefore remains exact.

## Burst test
```bash
./burst/burst.sh http://localhost:8080 <SHOW_ID> 500
```
For a hot-seat storm, exactly one request should be 201 and the rest 409.
