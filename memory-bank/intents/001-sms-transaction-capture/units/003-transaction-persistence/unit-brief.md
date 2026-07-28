---
unit: 003-transaction-persistence
intent: 001-sms-transaction-capture
phase: inception
status: ready
created: 2026-07-28T18:45:05Z
updated: 2026-07-28T18:45:05Z
---

# Unit Brief: Transaction Persistence

## Purpose

Provide the durable local store for everything the capture pipeline produces: Room entities for `Transaction` and `SMSLog`, a repository layer exposing Flow-based queries, and duplicate-detection logic so resent or paired bank SMS never create two records for the same underlying event.

## Scope

### In Scope
- Room entities: `Transaction`, `SMSLog`
- Repository layer (insert, query via Flow)
- Duplicate-detection key and dedup-on-insert logic
- Unified insert path used by both historical import and live detection results

### Out of Scope
- Acquiring or filtering SMS (→ `001-sms-ingestion`)
- Parsing/classification (→ `002-transaction-parser`)
- Category assignment, dashboard queries, budget queries (later intents build on this schema but don't belong here)

---

## Assigned Requirements

| FR | Requirement | Priority |
|----|-------------|----------|
| FR-9 | Duplicate Detection | Must |
| FR-10 | Local Persistence | Must |

---

## Domain Concepts

### Key Entities
| Entity | Description | Attributes |
|--------|-------------|------------|
| Transaction | A confirmed, parsed transaction record | id, amount, currency, date, time, bank, maskedAccountNumber, merchant, transactionType, dedupKey, sourceSmsLogId |
| SMSLog | Raw record of every scanned transaction-relevant SMS | id, sender, rawBody, timestamp, needsReview, linkedTransactionId (nullable) |

### Key Operations
| Operation | Description | Inputs | Outputs |
|-----------|-------------|--------|---------|
| InsertTransaction | Persist a parsed transaction, deduping against existing rows | ParsedTransaction | Transaction (new or existing linked row) |
| LogSms | Persist a raw SMS record, matched or flagged | RawSmsMessage/UnrecognizedSms | SMSLog |
| ComputeDedupKey | Derive a stable key identifying the same underlying event | sender, amount, timestamp, reference no. (if present) | dedupKey |
| QueryTransactions | Flow-based read access for later intents | filters (none in this intent — pass-through repository API) | Flow\<List\<Transaction\>\> |

---

## Story Summary

| Metric | Count |
|--------|-------|
| Total Stories | 3 |
| Must Have | 3 |
| Should Have | 0 |
| Could Have | 0 |

### Stories

| Story ID | Title | Priority | Status |
|----------|-------|----------|--------|
| 001-define-room-schema-transaction-smslog | Room entities + repository for Transaction/SMSLog | Must | Planned |
| 002-deduplicate-transactions-on-insert | Dedup key logic on insert | Must | Planned |
| 003-persist-import-and-live-results | Unified insert path for import + live pipelines | Must | Planned |

---

## Dependencies

### Depends On
| Unit | Reason |
|------|--------|
| 002-transaction-parser | Consumes `ParsedTransaction` / `UnrecognizedSms` output to persist |

### Depended By
| Unit | Reason |
|------|--------|
| (future) 002-transaction-management | Reads `Transaction` for categorization, search, filters |
| (future) 003-dashboard-analytics | Reads `Transaction` for KPIs and charts |
| (future) 004-budget-tracking | Reads `Transaction` for budget calculations |

### External Dependencies
| System | Purpose | Risk |
|--------|---------|------|
| None | Local Room/SQLite only | N/A |

---

## Technical Context

### Suggested Technology
Room (entities, DAOs, `Flow`-returning queries), Hilt for repository injection, Kotlin Coroutines for suspend insert functions. (Formal data-stack standard not yet created — recommend establishing one before/alongside this bolt.)

### Integration Points
| Integration | Type | Protocol |
|-------------|------|----------|
| 002-transaction-parser | In-process | Consumes `ParsedTransaction` / `UnrecognizedSms` |

### Data Storage
| Data | Type | Volume | Retention |
|------|------|--------|-----------|
| Transaction | Room/SQLite table | Up to ~10,000+ rows (6mo import + ongoing) | Indefinite (user-controlled deletion is out of scope for this intent) |
| SMSLog | Room/SQLite table | 1 row per scanned transaction-relevant SMS | Indefinite |

---

## Constraints

- No transaction data may leave the device from this unit (no network calls)
- Dedup must not silently discard the raw SMS — `SMSLog` is written even when a `Transaction` insert is deduped against an existing row

---

## Success Criteria

### Functional
- [ ] `Transaction` and `SMSLog` entities exist with the fields listed above and are queryable via repository
- [ ] Resent/paired SMS describing the same event do not create a second `Transaction` row
- [ ] Every scanned transaction-relevant SMS (matched or flagged) produces exactly one `SMSLog` row

### Non-Functional
- [ ] Insert path handles 10,000+ historical rows without blocking the UI thread (batched/transactional inserts)

### Quality
- [ ] Code coverage > 80%
- [ ] All acceptance criteria met
- [ ] Code reviewed and approved

---

## Bolt Suggestions

| Bolt | Type | Stories | Objective |
|------|------|---------|-----------|
| 005-transaction-persistence | DDD | 001, 002, 003 | Room schema, dedup logic, and unified persistence path |

---

## Notes

This unit's schema is the foundation every downstream intent (002 through 006) will read from — treat `Transaction`/`SMSLog` field naming and types as a stable contract once Construction completes this bolt.
