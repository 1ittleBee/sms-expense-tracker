---
unit: 001-sms-ingestion
intent: 001-sms-transaction-capture
phase: inception
status: ready
created: 2026-07-28T18:45:05Z
updated: 2026-07-28T18:45:05Z
---

# Unit Brief: SMS Ingestion

## Purpose

Own the boundary between the Android SMS subsystem and the rest of the app. Request and manage `READ_SMS`/`RECEIVE_SMS` permission, pull historical SMS bounded to the last 6 months, listen for live incoming SMS, and filter out non-transactional noise (OTP, promotional) before anything reaches the parser.

## Scope

### In Scope
- Runtime permission request flow with rationale and denial/retry handling
- Historical SMS import (last 6 months), backgrounded
- Live SMS detection via `BroadcastReceiver`
- Transaction-relevance filtering (exclude OTP/promotional SMS)

### Out of Scope
- Parsing SMS content into structured transaction fields (→ `002-transaction-parser`)
- Persisting anything to Room (→ `003-transaction-persistence`)
- Sending SMS of any kind (never in scope for the whole app)

---

## Assigned Requirements

| FR | Requirement | Priority |
|----|-------------|----------|
| FR-1 | SMS Read Permission Request | Must |
| FR-2 | Historical SMS Import (bounded to 6 months) | Must |
| FR-3 | Live SMS Detection | Must |
| FR-4 | Transaction-Relevance Filtering | Must |

---

## Domain Concepts

### Key Entities
| Entity | Description | Attributes |
|--------|-------------|------------|
| PermissionState | Current state of SMS permission | granted, denied, denied-permanently |
| RawSmsMessage | An SMS as read from the Android subsystem, pre-parsing | sender, body, timestamp, source (import/live) |

### Key Operations
| Operation | Description | Inputs | Outputs |
|-----------|-------------|--------|---------|
| RequestPermission | Trigger rationale + system permission dialog | none | PermissionState |
| ImportHistoricalSms | Query SMS provider for last 6 months, filter, emit | permission grant | Stream of filtered RawSmsMessage |
| ObserveLiveSms | Register receiver, filter incoming SMS | permission grant | Stream of filtered RawSmsMessage |
| IsTransactionRelevant | Exclude OTP/promotional SMS | RawSmsMessage | boolean |

---

## Story Summary

| Metric | Count |
|--------|-------|
| Total Stories | 5 |
| Must Have | 5 |
| Should Have | 0 |
| Could Have | 0 |

### Stories

| Story ID | Title | Priority | Status |
|----------|-------|----------|--------|
| 001-request-sms-permission | Request SMS permission with rationale | Must | Planned |
| 002-handle-permission-denial-retry | Handle permission denial and retry | Must | Planned |
| 003-import-historical-transaction-sms | Import last 6 months of transaction SMS | Must | Planned |
| 004-filter-non-transactional-sms | Filter out OTP and promotional SMS | Must | Planned |
| 005-detect-live-incoming-sms | Detect and forward new SMS live | Must | Planned |

---

## Dependencies

### Depends On
| Unit | Reason |
|------|--------|
| None | Entry point of the capture pipeline |

### Depended By
| Unit | Reason |
|------|--------|
| 002-transaction-parser | Consumes the stream of filtered, transaction-relevant `RawSmsMessage` |

### External Dependencies
| System | Purpose | Risk |
|--------|---------|------|
| Android SMS Content Provider (`content://sms`) | Historical SMS query for import | Low — standard read-only API |
| Android Telephony Broadcast (`SMS_RECEIVED_ACTION`) | Live SMS detection | Medium — receiver registration/behavior varies across Android versions and OEM SMS-app restrictions |

---

## Technical Context

### Suggested Technology
Kotlin, `ContentResolver` for `content://sms` queries, manifest-registered or runtime-registered `BroadcastReceiver` for `SMS_RECEIVED_ACTION`, WorkManager or a coroutine-based background job for the bounded historical import, Hilt for DI. (Formal tech-stack standard not yet created — see inception-log open item.)

### Integration Points
| Integration | Type | Protocol |
|-------------|------|----------|
| 002-transaction-parser | In-process | Kotlin Flow of filtered `RawSmsMessage` |

### Data Storage
| Data | Type | Volume | Retention |
|------|------|--------|-----------|
| None (this unit is stateless/pass-through) | N/A | N/A | N/A |

---

## Constraints

- Must never request or use `SEND_SMS`
- Historical import strictly bounded to 6 months from import time
- Receiver must never call `abortBroadcast()` or write to the SMS provider

---

## Success Criteria

### Functional
- [ ] Permission rationale shown before system dialog; denial explained with working retry
- [ ] Historical import completes for messages within the last 6 months only
- [ ] Live SMS forwarded to the parser stream within 3 seconds of arrival
- [ ] OTP and promotional SMS are excluded before reaching the parser

### Non-Functional
- [ ] Historical import of ~10,000 SMS runs in background without ANR
- [ ] No `SEND_SMS` permission declared or used

### Quality
- [ ] Code coverage > 80%
- [ ] All acceptance criteria met
- [ ] Code reviewed and approved

---

## Bolt Suggestions

| Bolt | Type | Stories | Objective |
|------|------|---------|-----------|
| 001-sms-ingestion | DDD | 001, 002 | Permission request, rationale, denial/retry |
| 002-sms-ingestion | DDD | 003, 004, 005 | Import, filtering, and live detection pipeline |

---

## Notes

Permission UX in this unit is intentionally minimal (just enough to unblock the pipeline) — the full Permission screen polish is expected to be revisited when the Dashboard/onboarding UI intent lands, but functionally this unit must be complete and usable standalone.
