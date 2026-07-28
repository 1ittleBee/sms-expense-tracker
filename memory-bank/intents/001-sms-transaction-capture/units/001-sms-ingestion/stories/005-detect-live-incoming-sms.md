---
id: 005-detect-live-incoming-sms
unit: 001-sms-ingestion
intent: 001-sms-transaction-capture
status: ready
priority: must
created: 2026-07-28T18:45:05Z
assigned_bolt: 002-sms-ingestion
implemented: false
---

# Story: 005-detect-live-incoming-sms

## User Story

**As a** device owner
**I want** new transaction SMS detected and processed the moment they arrive
**So that** my transaction data stays current without manual refresh

## Acceptance Criteria

- [ ] **Given** SMS permission is granted, **When** a new SMS arrives, **Then** a `BroadcastReceiver` observes it via `SMS_RECEIVED_ACTION`
- [ ] **Given** the received SMS passes the transaction-relevance filter (story 004), **When** filtering completes, **Then** it is forwarded to the parser stream within 3 seconds of arrival
- [ ] **Given** the received SMS does not pass the filter, **When** filtering completes, **Then** it is dropped without being forwarded, and the original SMS is left completely untouched
- [ ] **Given** the receiver processes any SMS, **When** inspected, **Then** it never calls `abortBroadcast()` and never writes to the SMS provider

## Technical Notes

- Register the receiver appropriately for the target Android version (manifest-registered may be restricted on newer Android releases; consider a foreground-service-backed dynamic receiver if needed — confirm against the assumed minSdk/targetSdk during Construction)
- Keep receiver `onReceive()` work minimal; hand off to a background coroutine/WorkManager for filtering and forwarding to avoid ANR on the broadcast

## Dependencies

### Requires
- 001-request-sms-permission
- 002-handle-permission-denial-retry
- 004-filter-non-transactional-sms

### Enables
- None further within this unit (hands off to `002-transaction-parser`)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Multiple SMS arrive in rapid succession | Each is processed independently; no messages dropped due to ordering/race conditions |
| App is in background/killed when SMS arrives | Receiver still fires (subject to OS/OEM background restrictions) and processing completes without requiring the UI to be open |
| Permission is revoked between registration and an incoming SMS | Receiver either isn't invoked (OS-enforced) or short-circuits safely without crashing |

## Out of Scope

- Persisting the transaction (→ `003-transaction-persistence`)
- Push notifications about the new transaction (later intent)
