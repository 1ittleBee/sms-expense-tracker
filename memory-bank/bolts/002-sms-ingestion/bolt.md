---
id: 002-sms-ingestion
unit: 001-sms-ingestion
intent: 001-sms-transaction-capture
type: ddd-construction-bolt
status: planned
stories:
  - 003-import-historical-transaction-sms
  - 004-filter-non-transactional-sms
  - 005-detect-live-incoming-sms
created: 2026-07-28T18:45:05Z
started: null
completed: null
current_stage: null
stages_completed: []

requires_bolts: [001-sms-ingestion]
enables_bolts: [003-transaction-parser]
requires_units: []
blocks: true

complexity:
  avg_complexity: 2
  avg_uncertainty: 2
  max_dependencies: 2
  testing_scope: 2
---

# Bolt: 002-sms-ingestion

## Overview

Second bolt of the SMS Ingestion unit: bounded historical import, the shared OTP/promotional filter, and live SMS detection via `BroadcastReceiver`.

## Objective

Deliver the full raw-SMS acquisition pipeline (historical + live), filtered down to transaction-relevant messages, ready to be consumed by the parser unit.

## Stories Included

- **003-import-historical-transaction-sms**: Import last 6 months of transaction SMS (Must)
- **004-filter-non-transactional-sms**: Filter out OTP and promotional SMS (Must)
- **005-detect-live-incoming-sms**: Detect and forward new SMS live (Must)

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
- 001-sms-ingestion (permission must be resolvable before import/live detection can run)

### Enables
- 003-transaction-parser (consumes this bolt's filtered `RawSmsMessage` stream)

## Success Criteria

- [ ] All stories implemented
- [ ] All acceptance criteria met
- [ ] Tests passing
- [ ] Code reviewed

## Notes

Confirm target Android version's `BroadcastReceiver` registration constraints (manifest vs. runtime registration, background execution limits) during the Technical Design stage — this affects story 005's implementation approach.
