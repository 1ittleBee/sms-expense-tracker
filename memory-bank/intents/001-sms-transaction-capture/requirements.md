---
intent: 001-sms-transaction-capture
phase: inception
status: inception-complete
created: 2026-07-28T18:45:05Z
updated: 2026-07-28T18:45:05Z
---

# Requirements: SMS Transaction Capture

## Intent Overview

Enable the SMS Expense Tracker app to request SMS read permission, import existing transaction-related SMS from the device, detect new incoming transaction SMS live, and parse both via a configurable per-bank/MFS regex engine — persisting structured transaction records locally in Room. This intent is foundational: every other intent (transaction management, dashboard, budgets, export) depends on the data this capability produces.

## Business Goals

| Goal | Success Metric | Priority |
|------|----------------|----------|
| Automatically capture transactions from bank/MFS SMS without manual entry | >90% of transaction SMS from supported senders parsed with correct amount/type | Must |
| Never send, modify, or exfiltrate SMS content | Zero outbound SMS or SEND_SMS permission use; no network calls from parser/import path | Must |
| Support the full range of BD banks and MFS providers named in scope | All 20 banks + 4 MFS providers (bKash, Nagad, Rocket, Upay) have at least one working parser pattern | Should |
| Handle real-world SMS volume without blocking the UI | Import of 10,000+ historical SMS completes in background without ANR | Must |

## Scope

**In scope:**
- READ_SMS permission request flow, including denial/rationale/retry handling
- Bulk import of existing SMS from device inbox, filtered to transaction-relevant messages
- Live detection of new SMS via BroadcastReceiver, parsed and saved immediately
- Regex-based parser engine, configurable per bank/MFS sender, extracting: amount, currency, date, time, bank, account number (masked), merchant, transaction type
- Classification into: Expense, Income, Transfer, ATM Withdrawal, Card Purchase, Refund
- Filtering out OTP and promotional SMS (non-transactional)
- Room persistence: `Transaction` and `SMSLog` entities
- Background/async processing (WorkManager or coroutines) for import and parsing

**Out of scope (deferred to later intents):**
- Category assignment beyond raw transaction type (→ 002-transaction-management)
- Dashboard/analytics/chart rendering of captured data (→ 003-dashboard-analytics)
- Budget tracking and notifications (→ 004-budget-tracking)
- Export/backup of transaction data (→ 005-export-backup)
- Settings UI, dark mode (→ 006-app-settings-appearance)

## Functional Requirements

### FR-1: SMS Read Permission Request
- **Description**: App must request `READ_SMS` (and `RECEIVE_SMS` for live detection) at runtime with a rationale screen explaining why access is needed before capturing transactions.
- **Acceptance Criteria**: On first launch, permission rationale is shown before the system dialog; if denied, app explains the impact and offers a retry action; app never requests `SEND_SMS`.
- **Priority**: Must
- **Related Stories**: 001-request-sms-permission, 002-handle-permission-denial-retry

### FR-2: Historical SMS Import (bounded)
- **Description**: On permission grant, import existing SMS from the device inbox, limited to the last 6 months, running as a background/async job that doesn't block the UI.
- **Acceptance Criteria**: Import starts automatically after grant; only messages within the last 6 months (from import time) are scanned; UI remains responsive (no ANR) throughout; user sees import progress.
- **Priority**: Must
- **Related Stories**: 003-import-historical-transaction-sms

### FR-3: Live SMS Detection
- **Description**: A `BroadcastReceiver` listens for incoming SMS in real time, passes transaction-relevant messages to the parser, and persists results immediately, without ever intercepting/aborting/modifying the SMS.
- **Acceptance Criteria**: New qualifying SMS is parsed and saved within seconds of arrival; receiver never calls `abortBroadcast()` or writes to the SMS provider; non-transactional SMS pass through untouched.
- **Priority**: Must
- **Related Stories**: 005-detect-live-incoming-sms

### FR-4: Transaction-Relevance Filtering
- **Description**: Before parsing, filter incoming/imported SMS to transaction-relevant messages only, excluding OTP codes and promotional/marketing SMS.
- **Acceptance Criteria**: SMS containing OTP-pattern keywords are excluded; promotional patterns are excluded; messages matching configured bank/MFS transaction keywords are retained.
- **Priority**: Must
- **Related Stories**: 004-filter-non-transactional-sms

### FR-5: Configurable Bank/MFS Parser Engine
- **Description**: A regex-based parser engine, data-driven per bank/MFS provider (not hard-coded per sender), supporting the 20 named banks and 4 MFS providers.
- **Acceptance Criteria**: Adding a new bank pattern requires only a new config entry, not new parser code; each supported bank/MFS has at least one passing sample-SMS test case; parser extracts amount, currency, date, time, bank, masked account number, merchant, transaction type from matched messages.
- **Priority**: Must
- **Related Stories**: 001-define-bank-parser-configuration, 002-parse-transaction-amount-and-metadata

### FR-6: Sender Identification with Fallback
- **Description**: Identify the originating bank/MFS primarily from SMS sender ID; when sender ID is generic or unrecognized, fall back to keyword matching within the message body.
- **Acceptance Criteria**: Known sender IDs map directly to a bank config; unrecognized sender IDs trigger body-keyword matching before falling back to "unknown bank".
- **Priority**: Should
- **Related Stories**: 003-identify-bank-from-sender-with-fallback

### FR-7: Transaction Type Classification
- **Description**: Classify each parsed transaction into one of: Expense, Income, Transfer, ATM Withdrawal, Card Purchase, Refund.
- **Acceptance Criteria**: Classification is derived from parsed keywords/patterns; every persisted transaction has a non-null type.
- **Priority**: Must
- **Related Stories**: 004-classify-transaction-type

### FR-8: Unrecognized SMS Handling
- **Description**: When a message from a supported sender doesn't match any known pattern, log it and surface it for manual user review rather than silently dropping it.
- **Acceptance Criteria**: Unmatched but sender-recognized SMS are written to `SMSLog` with raw body and a `needs_review` flag; queryable by later UI.
- **Priority**: Should
- **Related Stories**: 005-flag-unrecognized-sms-for-review

### FR-9: Duplicate Detection
- **Description**: Detect and prevent duplicate transaction records arising from resent SMS or paired messages describing the same transaction.
- **Acceptance Criteria**: A dedup key (sender + amount + timestamp window + reference no. when present) prevents inserting a second `Transaction` row for the same event; the raw SMS is still logged in `SMSLog` even when deduped.
- **Priority**: Must
- **Related Stories**: 002-deduplicate-transactions-on-insert

### FR-10: Local Persistence
- **Description**: Persist parsed transactions and raw SMS logs locally via Room (`Transaction`, `SMSLog` entities), accessible through a repository layer.
- **Acceptance Criteria**: Every successfully parsed transaction is queryable via repository; every scanned transaction-relevant SMS has a corresponding `SMSLog` row; no transaction data leaves the device.
- **Priority**: Must
- **Related Stories**: 001-define-room-schema-transaction-smslog, 003-persist-import-and-live-results

## Non-Functional Requirements

### Performance
| Requirement | Metric | Target |
|-------------|--------|--------|
| Historical import throughput | Time to scan/parse 6 months of SMS (~up to 10,000 messages) | No UI-thread blocking; completes in background without ANR |
| Live detection latency | Time from SMS arrival to persisted record | < 3 seconds |

### Security
| Requirement | Standard | Notes |
|-------------|----------|-------|
| Read-only SMS access | Android runtime permissions | Only `READ_SMS`/`RECEIVE_SMS`; `SEND_SMS` never requested or used |
| No data exfiltration | N/A | No network calls in the import/parse/persist path; all data stays local |
| Local storage | Room (SQLite) | Encryption deferred for this intent (see Assumptions) |

### Reliability
| Requirement | Metric | Target |
|-------------|--------|--------|
| Parse failure handling | % of sender-recognized SMS logged (matched or flagged) | 100% |
| Import resumability | Behavior on interruption | Import can resume/re-run without creating duplicate records (relies on FR-9 dedup) |

### Compatibility
| Requirement | Metric | Target |
|-------------|--------|--------|
| Android version support | minSdk / targetSdk | Assumed `minSdk 26` (Android 8.0) / latest stable `targetSdk` (accepted default) |

## Constraints

### Technical Constraints

**Project-wide standards**: Not yet established — no `memory-bank/standards/` created. Known tech direction from user brief (to be formalized as standards during/after this intent): Kotlin, Jetpack Compose, Material Design 3, MVVM + Clean Architecture, Room, Hilt, Coroutines/Flow, DataStore.

**Intent-specific constraints**:
- Must never request or use `SEND_SMS` — read-only access only
- Parser must be data-driven/configurable per bank, not hard-coded per-sender logic, to allow adding banks without touching core parsing engine

### Business Constraints
- Single-user, on-device app — no server-side component for this intent

## Assumptions

| Assumption | Risk if Invalid | Mitigation |
|------------|-----------------|------------|
| Bank/MFS SMS formats are reasonably consistent per sender over time | Parser silently fails or misclassifies on format drift | Log unparsed SMS to `SMSLog` with raw body for review; design parser rules to be updatable without app update where feasible |
| Android SMS permission model (READ_SMS, SmsRetriever/BroadcastReceiver) is available on target API levels | Live detection may be restricted on newer Android versions | Confirm target/minSdk during system-context step; validate against latest Play Store SMS policy restrictions |

## Open Questions

| Question | Owner | Due Date | Resolution |
|----------|-------|----------|------------|
| Exact minSdk/targetSdk given Play Store's SMS permission policy restrictions | User | Before context step | Resolved — accepted assumed default (`minSdk 26`, latest `targetSdk`); personal/sideload distribution confirmed |
| Should Room database be encrypted (e.g., SQLCipher) as part of this intent, or deferred? | User | Before construction | Resolved — deferred; plain Room/SQLite for this intent, encryption flagged as future hardening |
