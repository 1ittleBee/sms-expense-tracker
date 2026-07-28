---
id: 005-transaction-persistence
unit: 003-transaction-persistence
intent: 001-sms-transaction-capture
type: ddd-construction-bolt
status: planned
stories:
  - 001-define-room-schema-transaction-smslog
  - 002-deduplicate-transactions-on-insert
  - 003-persist-import-and-live-results
created: 2026-07-28T18:45:05Z
started: null
completed: null
current_stage: null
stages_completed: []

requires_bolts: [004-transaction-parser]
enables_bolts: []
requires_units: [002-transaction-parser]
blocks: true

complexity:
  avg_complexity: 2
  avg_uncertainty: 1
  max_dependencies: 2
  testing_scope: 2
---

# Bolt: 005-transaction-persistence

## Overview

Only bolt of the Transaction Persistence unit: Room schema for `Transaction`/`SMSLog`, dedup-on-insert logic, and the unified insert path shared by import and live pipelines.

## Objective

Close out the intent's pipeline with a durable, deduplicated local store that all downstream intents (transaction-management, dashboard-analytics, budget-tracking, etc.) will read from.

## Stories Included

- **001-define-room-schema-transaction-smslog**: Room entities + repository for Transaction/SMSLog (Must)
- **002-deduplicate-transactions-on-insert**: Dedup key logic on insert (Must)
- **003-persist-import-and-live-results**: Unified insert path for import + live pipelines (Must)

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
- 004-transaction-parser (bolt): needs `ParsedTransaction`/`UnrecognizedSms` shapes finalized
- 002-transaction-parser (unit): must be complete before end-to-end pipeline testing

### Enables
- None further within this intent — unblocks intents 002 through 006, which all read from this schema

## Success Criteria

- [ ] All stories implemented
- [ ] All acceptance criteria met
- [ ] Tests passing
- [ ] Code reviewed

## Notes

This bolt's schema is a stable contract for every later intent. Treat any post-hoc field renames/type changes here as a breaking change requiring coordination, not a casual refactor.
