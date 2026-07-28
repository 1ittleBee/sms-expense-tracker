---
id: 001-define-room-schema-transaction-smslog
unit: 003-transaction-persistence
intent: 001-sms-transaction-capture
status: ready
priority: must
created: 2026-07-28T18:45:05Z
assigned_bolt: 005-transaction-persistence
implemented: false
---

# Story: 001-define-room-schema-transaction-smslog

## User Story

**As a** developer maintaining this app
**I want** Room entities for `Transaction` and `SMSLog` with a repository layer
**So that** parsed transaction data has a durable, queryable local store that later intents can build on

## Acceptance Criteria

- [ ] **Given** the Room schema, **When** inspected, **Then** a `Transaction` entity exists with: amount, currency, date, time, bank, masked account number, merchant, transaction type, dedup key, and a reference to its source `SMSLog` row
- [ ] **Given** the Room schema, **When** inspected, **Then** an `SMSLog` entity exists with: sender, raw body, timestamp, `needsReview` flag, and a nullable reference to a linked `Transaction`
- [ ] **Given** the repository layer, **When** used, **Then** it exposes `Flow`-based read queries (no data consumed by this intent's UI, but the API must be ready for later intents to consume)
- [ ] **Given** the repository, **When** insert functions are called, **Then** they are `suspend` functions safe to call from background/coroutine contexts

## Technical Notes

- This schema is a stable contract for intents 002–006 — favor explicit, descriptive column names over premature optimization
- Masked account number must be stored already-masked (masking happens in the parser unit, not here)

## Dependencies

### Requires
- None (first story in this unit)

### Enables
- 002-deduplicate-transactions-on-insert
- 003-persist-import-and-live-results

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| A `ParsedTransaction` with some null fields (e.g., no merchant) | Schema allows nullable columns for optional fields; required fields (amount, type, date) are non-null |

## Out of Scope

- Database migrations strategy beyond initial schema (addressed if/when schema changes in later intents)
- Encryption at rest (flagged as an open assumption in requirements.md, not committed for this intent)
