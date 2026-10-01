# SeatReserve — Engineering Write-up

## Atomic decision
The atomic decision lives in PostgreSQL. The reservation service runs in one transaction and locks all requested seat rows with `SELECT ... FOR UPDATE` through JPA `PESSIMISTIC_WRITE`. Seat numbers are sorted before locking, so concurrent multi-seat requests acquire locks in the same order. A seat is only changed from AVAILABLE to CONFIRMED while its row is locked.

## Idempotency
The `(user_id, show_id, idempotency_key)` tuple has a database unique constraint. The stored request fingerprint is SHA-256 over the canonical sorted seat list. A retry with the same key and same body returns the original reservation. The same key with a different seat list returns 409.

## Per-user limit
The service checks the number of currently confirmed seats for the authenticated user before locking seats. The final reservation is created inside the same transaction as the seat locks. For a strict production variant under extreme concurrent requests, a per-show/user counter row can be locked as part of the same deterministic transaction to make the limit decision itself database-serialized.

## Holds / release
This implementation chooses explicit cancellation rather than timed holds. A cancellation is owner-only and transitions the reservation to CANCELLED and its seats back to AVAILABLE in one transaction.

## Consistency vs availability
The service favors consistency for seat ownership. PostgreSQL is the source of truth; if the database is unavailable, readiness fails and reservation traffic should not be accepted. During a partition, the service does not attempt an eventually-consistent seat decision.

## Observability
Actuator exposes health and Prometheus metrics. Reservation counters are split by confirmed and decline reason. Structured request IDs are returned in `X-Request-Id`. The key 2am alerts would be DB/readiness failures, sustained 5xx, connection-pool exhaustion, reservation latency spikes, and reconciliation mismatches.

## AI usage
AI was used as an implementation assistant for project scaffolding, boilerplate generation, API examples, and review prompts. The final concurrency model, database constraints, transactional boundaries, and operational trade-offs should be understood and defended by the author during the interview.

## Next steps
- Add a durable per-user/show counter row to make the limit check fully serialized under arbitrary concurrency.
- Add a dedicated load-test module and a 20k-request burst runner.
- Add integration tests that prove exactly one winner for a hot seat.
- Add deployment configuration and dashboard examples.
