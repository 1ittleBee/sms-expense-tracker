---
id: 003-import-historical-transaction-sms
unit: 001-sms-ingestion
intent: 001-sms-transaction-capture
status: ready
priority: must
created: 2026-07-28T18:45:05Z
assigned_bolt: 002-sms-ingestion
implemented: false
---

# Story: 003-import-historical-transaction-sms

## User Story

**As a** device owner who just granted SMS permission
**I want** the app to automatically import my transaction SMS from the last 6 months
**So that** I have historical transaction data without manual entry

## Acceptance Criteria

- [ ] **Given** SMS permission is freshly granted, **When** the app detects the grant, **Then** it automatically starts a background import job (no user action required beyond the grant)
- [ ] **Given** the import job runs, **When** it queries the SMS provider, **Then** only messages with a timestamp within the last 6 months (from import start time) are scanned
- [ ] **Given** the import job is running, **When** observed from the UI, **Then** the main thread remains responsive (no ANR) and a progress indicator is shown
- [ ] **Given** the import completes, **When** finished, **Then** all qualifying (transaction-relevant, per story 004) messages have been forwarded to the parser stream

## Technical Notes

- Run via WorkManager or a dedicated background coroutine scope; do not block the UI thread
- Query `content://sms/inbox` via `ContentResolver`, paginated/batched for large inboxes (10,000+ messages)
- 6-month boundary computed at import start time, not hardcoded to a calendar date

## Dependencies

### Requires
- 001-request-sms-permission
- 002-handle-permission-denial-retry (import only starts once permission is actually granted, including via retry path)

### Enables
- 004-filter-non-transactional-sms (import feeds through the same filter as live detection)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| App is killed mid-import | On next launch with permission still granted, import can safely re-run without producing duplicate transactions (dedup handled downstream in `003-transaction-persistence`) |
| Device has fewer than 6 months of SMS history | Import scans whatever exists; no error |
| Extremely large inbox (10,000+ messages) | Import completes in background without ANR; batched/paginated querying |

## Out of Scope

- Configurable import window (fixed at 6 months per approved requirements)
- Deduplication logic itself (owned by `003-transaction-persistence`)
