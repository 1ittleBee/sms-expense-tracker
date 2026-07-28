---
id: 004-transaction-parser
unit: 002-transaction-parser
intent: 001-sms-transaction-capture
type: ddd-construction-bolt
status: planned
stories:
  - 003-identify-bank-from-sender-with-fallback
  - 004-classify-transaction-type
  - 005-flag-unrecognized-sms-for-review
created: 2026-07-28T18:45:05Z
started: null
completed: null
current_stage: null
stages_completed: []

requires_bolts: [003-transaction-parser]
enables_bolts: [005-transaction-persistence]
requires_units: []
blocks: true

complexity:
  avg_complexity: 2
  avg_uncertainty: 2
  max_dependencies: 2
  testing_scope: 1
---

# Bolt: 004-transaction-parser

## Overview

Second bolt of the Transaction Parser unit: sender identification with fallback, transaction type classification, and flagging of unparseable-but-known-sender SMS.

## Objective

Complete the parser engine by adding bank attribution (with fallback), deterministic transaction-type classification, and the unrecognized-SMS contract that persistence will consume.

## Stories Included

- **003-identify-bank-from-sender-with-fallback**: Identify bank via sender ID with body-keyword fallback (Should)
- **004-classify-transaction-type**: Classify into Expense/Income/Transfer/ATM/Card/Refund (Must)
- **005-flag-unrecognized-sms-for-review**: Flag unparseable-but-known-sender SMS for review (Should)

## Bolt Type

**Type**: DDD Construction Bolt
**Definition**: `.specsmd/aidlc/templates/construction/bolt-types/ddd-construction-bolt.md`

## Stages

- [ ] **1. model**: Pending → ddd-01-domain-model.md
- [ ] **2. design**: Pending → ddd-02-technical-design.md
- [ ] **3. adr** (optional): Pending → adr-*.md
- [ ] **4. implement**: Pending → source code
- [ ] **5. test**: Pending → ddd-03-test-report.md

## Dependencies

### Requires
- 003-transaction-parser (needs the config format and extraction engine from that bolt)

### Enables
- 005-transaction-persistence (consumes `ParsedTransaction` and `UnrecognizedSms` produced here)

## Success Criteria

- [ ] All stories implemented
- [ ] All acceptance criteria met
- [ ] Tests passing
- [ ] Code reviewed

## Notes

Document the deterministic keyword-precedence order for transaction-type classification explicitly in the Domain Model stage — this is the kind of decision worth an ADR if any non-obvious trade-off is made.
