---
unit: 002-transaction-parser
intent: 001-sms-transaction-capture
phase: inception
status: ready
created: 2026-07-28T18:45:05Z
updated: 2026-07-28T18:45:05Z
---

# Unit Brief: Transaction Parser

## Purpose

Turn a filtered, transaction-relevant SMS into structured transaction data. Own the configurable per-bank/MFS parsing rules, sender identification (with body-keyword fallback), field extraction, and transaction type classification — all as a data-driven engine, not per-sender hard-coded logic.

## Scope

### In Scope
- Data-driven parser configuration format for 20 banks + 4 MFS providers
- Field extraction: amount, currency, date, time, bank, masked account number, merchant
- Sender identification with body-keyword fallback for generic/unrecognized sender IDs
- Transaction type classification (Expense, Income, Transfer, ATM Withdrawal, Card Purchase, Refund)
- Flagging of sender-recognized-but-unparseable SMS for review

### Out of Scope
- Acquiring/filtering raw SMS (→ `001-sms-ingestion`)
- Persisting parsed results or the review flag (→ `003-transaction-persistence`)
- Category assignment beyond raw transaction type (→ later intent `002-transaction-management`)

---

## Assigned Requirements

| FR | Requirement | Priority |
|----|-------------|----------|
| FR-5 | Configurable Bank/MFS Parser Engine | Must |
| FR-6 | Sender Identification with Fallback | Should |
| FR-7 | Transaction Type Classification | Must |
| FR-8 | Unrecognized SMS Handling | Should |

---

## Domain Concepts

### Key Entities
| Entity | Description | Attributes |
|--------|-------------|------------|
| BankParserConfig | Data-driven rule set for one bank/MFS provider | senderIds, bodyKeywords, fieldRegexPatterns, transactionTypeRules |
| ParsedTransaction | Structured result of a successful parse | amount, currency, date, time, bank, maskedAccountNumber, merchant, transactionType |
| UnrecognizedSms | Result when a sender-recognized SMS doesn't match any pattern | rawBody, sender, reason |

### Key Operations
| Operation | Description | Inputs | Outputs |
|-----------|-------------|--------|---------|
| IdentifyBank | Match sender ID, falling back to body keywords | RawSmsMessage | BankParserConfig or "unknown" |
| ExtractFields | Apply regex rules to pull structured data | RawSmsMessage, BankParserConfig | ParsedTransaction (partial fields allowed) |
| ClassifyTransactionType | Determine type from extracted/matched keywords | ParsedTransaction (partial) | ParsedTransaction (with type) |
| HandleUnmatched | Produce a flagged record when parsing fails for a known sender | RawSmsMessage | UnrecognizedSms |

---

## Story Summary

| Metric | Count |
|--------|-------|
| Total Stories | 5 |
| Must Have | 3 |
| Should Have | 2 |
| Could Have | 0 |

### Stories

| Story ID | Title | Priority | Status |
|----------|-------|----------|--------|
| 001-define-bank-parser-configuration | Data-driven parser config for banks/MFS | Must | Planned |
| 002-parse-transaction-amount-and-metadata | Extract amount/currency/date/time/bank/account/merchant | Must | Planned |
| 003-identify-bank-from-sender-with-fallback | Identify bank via sender ID with body-keyword fallback | Should | Planned |
| 004-classify-transaction-type | Classify into Expense/Income/Transfer/ATM/Card/Refund | Must | Planned |
| 005-flag-unrecognized-sms-for-review | Flag unparseable-but-known-sender SMS for review | Should | Planned |

---

## Dependencies

### Depends On
| Unit | Reason |
|------|--------|
| 001-sms-ingestion | Consumes the stream of filtered, transaction-relevant `RawSmsMessage` |

### Depended By
| Unit | Reason |
|------|--------|
| 003-transaction-persistence | Consumes `ParsedTransaction` and `UnrecognizedSms` output to persist |

### External Dependencies
| System | Purpose | Risk |
|--------|---------|------|
| None | Pure in-process parsing logic, no external systems | N/A |

---

## Technical Context

### Suggested Technology
Kotlin regex (`Regex` class), a config format (e.g., bundled JSON/YAML or Kotlin data objects) for the 24 bank/MFS rule sets, unit-testable pure functions for extraction/classification. (Formal tech-stack standard not yet created.)

### Integration Points
| Integration | Type | Protocol |
|-------------|------|----------|
| 001-sms-ingestion | In-process | Consumes Kotlin Flow of filtered `RawSmsMessage` |
| 003-transaction-persistence | In-process | Emits `ParsedTransaction` / `UnrecognizedSms` for persistence |

### Data Storage
| Data | Type | Volume | Retention |
|------|------|--------|-----------|
| Bank/MFS parser config | Bundled config (JSON/YAML or code) | 24 entries | Ships with app; updatable via app update |

---

## Constraints

- Parser must be config-driven per bank — adding a bank/MFS must not require new parser engine code
- Must produce a result (matched or flagged-unrecognized) for every sender-recognized transaction-relevant SMS — never silently dropping one

---

## Success Criteria

### Functional
- [ ] All 24 banks/MFS have at least one passing sample-SMS test case
- [ ] Sender identification falls back to body keywords for generic sender IDs
- [ ] Every parsed transaction has a non-null transaction type
- [ ] Unparseable-but-known-sender SMS produce a flagged `UnrecognizedSms` result

### Non-Functional
- [ ] Parsing is synchronous/fast enough not to be the bottleneck in the 3-second live-detection budget (owned jointly with Unit 1)

### Quality
- [ ] Code coverage > 80%
- [ ] All acceptance criteria met
- [ ] Code reviewed and approved

---

## Bolt Suggestions

| Bolt | Type | Stories | Objective |
|------|------|---------|-----------|
| 003-transaction-parser | DDD | 001, 002 | Parser engine core: config format + field extraction |
| 004-transaction-parser | DDD | 003, 004, 005 | Sender identification, classification, and unrecognized-SMS handling |

---

## Notes

This unit carries the highest domain complexity in the intent (24 distinct bank/MFS formats). Recommend collecting real (anonymized) sample SMS per bank during Construction's Domain Model stage to ground the regex patterns rather than guessing formats.
