---
id: 003-persist-import-and-live-results
unit: 003-transaction-persistence
intent: 001-sms-transaction-capture
status: ready
priority: must
created: 2026-07-28T18:45:05Z
assigned_bolt: 005-transaction-persistence
implemented: false
---

# Story: 003-persist-import-and-live-results

## User Story

**As a** device owner
**I want** both historical import and live-detected transactions saved to the same store
**So that** all my transaction data is unified regardless of how it was captured

## Acceptance Criteria

- [ ] **Given** a `ParsedTransaction` from the historical import pipeline (Unit 1 → Unit 2), **When** it reaches persistence, **Then** it is inserted through the same repository path (including dedup) as live-detected transactions
- [ ] **Given** a `ParsedTransaction` from the live-detection pipeline, **When** it reaches persistence, **Then** it is inserted through the identical repository path — no separate/divergent code path for "live" vs "imported" data
- [ ] **Given** an `UnrecognizedSms` from either pipeline, **When** it reaches persistence, **Then** an `SMSLog` row with `needsReview = true` is created regardless of origin
- [ ] **Given** both pipelines writing concurrently (e.g., import still running when a live SMS arrives), **When** both attempt inserts, **Then** no data race or corruption occurs (writes are properly synchronized via Room's transactional guarantees)

## Technical Notes

- A single repository method (e.g., `recordParsedResult(...)`) should be the sole entry point used by both Unit 1/2 pipelines, tagged with a `source` field (import/live) for observability but not for divergent logic
- Rely on Room's transaction support for insert atomicity when concurrent writes are possible

## Dependencies

### Requires
- 001-define-room-schema-transaction-smslog
- 002-deduplicate-transactions-on-insert

### Enables
- None further within this intent — this closes the pipeline; later intents (transaction-management, dashboard, budgets) read from this store

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Import job and live receiver both try to insert the same transaction at nearly the same instant | Dedup logic (story 002) + Room transactional insert prevent a duplicate row regardless of race |
| App restarts mid-import; import resumes and reprocesses some already-imported SMS | Dedup prevents re-creating already-persisted `Transaction` rows |

## Out of Scope

- Read-side query optimization for dashboard/analytics (later intents own their specific query needs against this schema)
