---
id: 001-sms-ingestion
unit: 001-sms-ingestion
intent: 001-sms-transaction-capture
type: ddd-construction-bolt
status: complete
stories:
  - 001-request-sms-permission
  - 002-handle-permission-denial-retry
created: '2026-07-28T18:45:05Z'
started: '2026-07-28T19:11:43Z'
completed: '2026-07-28T19:28:32Z'
current_stage: null
stages_completed:
  - name: model
    completed: '2026-07-28T19:12:54Z'
    artifact: ddd-01-domain-model.md
  - name: design
    completed: '2026-07-28T19:14:22Z'
    artifact: ddd-02-technical-design.md
  - name: adr
    completed: '2026-07-28T19:14:22Z'
    artifact: null
  - name: implement
    completed: '2026-07-28T19:21:12Z'
    artifact: src/app/src/main/kotlin/com/trackapp/smsexpensetracker/smsingestion/
requires_bolts: []
enables_bolts:
  - 002-sms-ingestion
requires_units: []
blocks: false
complexity:
  avg_complexity: 1
  avg_uncertainty: 1
  max_dependencies: 1
  testing_scope: 2
---

# Bolt: 001-sms-ingestion

## Overview

First bolt of the SMS Ingestion unit: the runtime permission flow, including rationale, denial handling, and retry (including the permanent-denial → Settings deep-link path).

## Objective

Get the app to a state where `READ_SMS`/`RECEIVE_SMS` permission is reliably obtained (or the user is clearly guided to obtain it), unblocking the import and live-detection work in the next bolt.

## Stories Included

- **001-request-sms-permission**: Request SMS permission with rationale (Must)
- **002-handle-permission-denial-retry**: Handle permission denial and retry (Must)

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
- None (first bolt in the intent)

### Enables
- 002-sms-ingestion (import/filter/live-detection depend on permission being resolvable)

## Success Criteria

- [ ] All stories implemented
- [ ] All acceptance criteria met
- [ ] Tests passing
- [ ] Code reviewed

## Notes

`SEND_SMS` must never appear in the manifest or be requested at runtime — verify this explicitly during the test stage.
