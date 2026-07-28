---
id: 003-transaction-parser
unit: 002-transaction-parser
intent: 001-sms-transaction-capture
type: ddd-construction-bolt
status: planned
stories:
  - 001-define-bank-parser-configuration
  - 002-parse-transaction-amount-and-metadata
created: 2026-07-28T18:45:05Z
started: null
completed: null
current_stage: null
stages_completed: []

requires_bolts: [002-sms-ingestion]
enables_bolts: [004-transaction-parser]
requires_units: [001-sms-ingestion]
blocks: true

complexity:
  avg_complexity: 3
  avg_uncertainty: 2
  max_dependencies: 2
  testing_scope: 1
---

# Bolt: 003-transaction-parser

## Overview

First bolt of the Transaction Parser unit: the config-driven parser engine foundation and field-extraction logic covering all 24 supported banks/MFS providers.

## Objective

Establish the parser engine's core config format and prove field extraction (amount, currency, date, time, bank, masked account, merchant) works across all 24 supported senders.

## Stories Included

- **001-define-bank-parser-configuration**: Data-driven parser config for banks/MFS (Must)
- **002-parse-transaction-amount-and-metadata**: Extract amount/currency/date/time/bank/account/merchant (Must)

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
- 002-sms-ingestion (bolt): needs the filtered `RawSmsMessage` stream
- 001-sms-ingestion (unit): must be complete before end-to-end testing is meaningful

### Enables
- 004-transaction-parser (sender identification, classification, and unrecognized-handling build on this core)

## Success Criteria

- [ ] All stories implemented
- [ ] All acceptance criteria met
- [ ] Tests passing
- [ ] Code reviewed

## Notes

Highest domain complexity in the intent — recommend gathering real (anonymized) sample SMS per bank/MFS during the Domain Model stage rather than guessing formats. Consider whether config should be compiled-in Kotlin data vs. externalized JSON during Technical Design.
