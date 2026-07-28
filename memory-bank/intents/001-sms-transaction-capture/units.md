---
intent: 001-sms-transaction-capture
phase: inception
status: units-decomposed
updated: 2026-07-28T18:45:05Z
---

# SMS Transaction Capture - Unit Decomposition

## Units Overview

This intent decomposes into 3 units of work (backend/domain units — no frontend unit, since this intent has no UI in scope):

### Unit 1: 001-sms-ingestion

**Description**: Requests SMS permission, imports historical SMS (bounded to 6 months), detects live incoming SMS, and filters out non-transactional (OTP/promotional) messages before handing transaction-relevant SMS onward.

**Stories**:

- 001-request-sms-permission
- 002-handle-permission-denial-retry
- 003-import-historical-transaction-sms
- 004-filter-non-transactional-sms
- 005-detect-live-incoming-sms

**Deliverables**:

- Permission request/rationale UI flow (minimal, capture-scoped)
- Background historical import job
- `BroadcastReceiver` for live SMS
- Transaction-relevance filter (OTP/promo exclusion)

**Dependencies**:

- Depends on: None (entry point of the pipeline)
- Depended by: `002-transaction-parser`

**Estimated Complexity**: M

### Unit 2: 002-transaction-parser

**Description**: Configurable, data-driven regex parser engine covering 20 banks + 4 MFS providers. Identifies the sending bank (sender ID, with body-keyword fallback), extracts transaction fields, classifies transaction type, and flags unrecognized-but-sender-known SMS for review.

**Stories**:

- 001-define-bank-parser-configuration
- 002-parse-transaction-amount-and-metadata
- 003-identify-bank-from-sender-with-fallback
- 004-classify-transaction-type
- 005-flag-unrecognized-sms-for-review

**Deliverables**:

- Parser engine + per-bank/MFS config data
- Sender identification with body-keyword fallback
- Transaction type classifier
- Unrecognized-SMS flagging contract (consumed by Unit 3's `SMSLog`)

**Dependencies**:

- Depends on: `001-sms-ingestion` (consumes filtered, transaction-relevant SMS)
- Depended by: `003-transaction-persistence`

**Estimated Complexity**: L

### Unit 3: 003-transaction-persistence

**Description**: Room schema for `Transaction` and `SMSLog`, repository layer, and duplicate-detection logic applied at insert time so resent/paired bank SMS don't create duplicate transaction records.

**Stories**:

- 001-define-room-schema-transaction-smslog
- 002-deduplicate-transactions-on-insert
- 003-persist-import-and-live-results

**Deliverables**:

- `Transaction` and `SMSLog` Room entities + DAOs
- Repository layer (Flow-based queries)
- Dedup key logic

**Dependencies**:

- Depends on: `002-transaction-parser` (consumes parsed/classified transaction data and unrecognized-SMS flags)
- Depended by: All later intents (002-transaction-management, 003-dashboard-analytics, etc.)

**Estimated Complexity**: M

## Unit Dependency Graph

```text
[001-sms-ingestion] ──> [002-transaction-parser] ──> [003-transaction-persistence]
```

Linear pipeline: raw SMS acquisition → parsing/classification → persistence. No fan-out/fan-in within this intent.

## Execution Order

1. `001-sms-ingestion` (foundation — nothing else can be tested end-to-end without it)
2. `002-transaction-parser` (consumes Unit 1's output)
3. `003-transaction-persistence` (consumes Unit 2's output; unblocks all downstream intents)
