---
id: 005-flag-unrecognized-sms-for-review
unit: 002-transaction-parser
intent: 001-sms-transaction-capture
status: ready
priority: should
created: 2026-07-28T18:45:05Z
assigned_bolt: 004-transaction-parser
implemented: false
---

# Story: 005-flag-unrecognized-sms-for-review

## User Story

**As a** device owner
**I want** SMS from a known bank that don't match any parsing pattern to be logged and flagged for my review instead of silently dropped
**So that** I don't lose transaction data when a bank changes its SMS format

## Acceptance Criteria

- [ ] **Given** an SMS whose sender resolves to a known bank/MFS (via 003's sender-ID or fallback match) but whose body doesn't match any field-extraction pattern, **When** parsing completes, **Then** an `UnrecognizedSms` result is produced (not a silent drop)
- [ ] **Given** an `UnrecognizedSms` result, **When** produced, **Then** it carries the raw SMS body, sender, and a reason (e.g., "no matching field pattern")
- [ ] **Given** an SMS whose sender does NOT resolve to any known bank at all, **When** parsing completes, **Then** it is treated per the "unknown bank" path from story 003, distinct from a known-bank-unparseable case
- [ ] **Given** the `UnrecognizedSms` result, **When** handed to `003-transaction-persistence`, **Then** it results in an `SMSLog` entry with `needs_review = true`

## Technical Notes

- This story defines the *contract* (the `UnrecognizedSms` result shape); actual persistence of the flag is owned by `003-transaction-persistence` story `001-define-room-schema-transaction-smslog`

## Dependencies

### Requires
- 002-parse-transaction-amount-and-metadata
- 003-identify-bank-from-sender-with-fallback

### Enables
- `003-transaction-persistence` / `001-define-room-schema-transaction-smslog` (consumes this contract)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Bank changes SMS wording after an app update breaks the regex | Message is flagged for review rather than crashing the parser or being silently discarded |
| Partial match (some fields extract, others don't) | Treated as a successful (if incomplete) parse per story 002's null-field handling, not as `UnrecognizedSms` — `UnrecognizedSms` is reserved for messages with no usable field match at all |

## Out of Scope

- The actual UI surface where the user reviews flagged messages (future dashboard/transactions intent)
