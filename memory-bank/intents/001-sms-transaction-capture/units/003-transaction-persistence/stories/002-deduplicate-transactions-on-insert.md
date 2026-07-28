---
id: 002-deduplicate-transactions-on-insert
unit: 003-transaction-persistence
intent: 001-sms-transaction-capture
status: ready
priority: must
created: 2026-07-28T18:45:05Z
assigned_bolt: 005-transaction-persistence
implemented: false
---

# Story: 002-deduplicate-transactions-on-insert

## User Story

**As a** device owner
**I want** duplicate transactions from resent or paired SMS to not create multiple records
**So that** my transaction history stays accurate and I'm not double-counted on spending

## Acceptance Criteria

- [ ] **Given** a `ParsedTransaction` about to be inserted, **When** a dedup key is computed (sender + amount + timestamp window + reference number if present), **Then** it is checked against existing `Transaction` rows before insert
- [ ] **Given** a matching dedup key already exists, **When** insert is attempted, **Then** no new `Transaction` row is created; the existing row is left as the canonical record
- [ ] **Given** a matching dedup key already exists, **When** the duplicate SMS is processed, **Then** its raw content is still written to `SMSLog` (linked to the existing `Transaction`), so no SMS is silently lost
- [ ] **Given** two distinct transactions that happen to share sender+amount within the timestamp window but have different reference numbers, **When** processed, **Then** both are persisted as separate `Transaction` rows (reference number distinguishes them)

## Technical Notes

- Timestamp window should be tunable (e.g., a few minutes) to account for bank/MFS resend delays without being so wide it merges genuinely separate same-amount transactions
- Reference number (when extractable from the SMS) should be preferred as the strongest dedup signal when present

## Dependencies

### Requires
- 001-define-room-schema-transaction-smslog

### Enables
- 003-persist-import-and-live-results

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Same transaction reported by both a debit SMS and a separate balance-update SMS | Both link to a single `Transaction` row via matching dedup key; second SMS's `SMSLog` row references the existing transaction |
| User genuinely makes two identical-amount purchases at the same merchant within the dedup window | Without a distinguishing reference number, this is a known limitation — documented, not silently "fixed" by over-merging |

## Out of Scope

- User-facing UI to manually merge/split transactions (later intent)
