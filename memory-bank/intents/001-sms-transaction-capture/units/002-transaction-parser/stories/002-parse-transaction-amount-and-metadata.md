---
id: 002-parse-transaction-amount-and-metadata
unit: 002-transaction-parser
intent: 001-sms-transaction-capture
status: ready
priority: must
created: 2026-07-28T18:45:05Z
assigned_bolt: 003-transaction-parser
implemented: false
---

# Story: 002-parse-transaction-amount-and-metadata

## User Story

**As a** device owner
**I want** the parser to extract amount, currency, date, time, bank, masked account number, and merchant from matched SMS
**So that** my transaction records are populated automatically without manual entry

## Acceptance Criteria

- [ ] **Given** an SMS matching a configured bank pattern (e.g., "Tk 500 withdrawn", "Debit BDT 1200", "Purchase BDT 900", "Cash Withdrawal", "ATM Withdrawal", "Salary Credited", "Deposit", "Transfer"), **When** parsed, **Then** amount and currency are correctly extracted
- [ ] **Given** the SMS contains a date/time (explicit or inferable from SMS receipt time when absent from body), **When** parsed, **Then** date and time fields are populated
- [ ] **Given** the SMS contains an account number, **When** parsed, **Then** it is extracted in masked form (e.g., last 4 digits only) — never stored/logged unmasked
- [ ] **Given** the SMS contains a merchant name (e.g., card purchase messages), **When** parsed, **Then** merchant is extracted; **when** absent (e.g., a plain withdrawal), merchant is left null rather than guessed
- [ ] **Given** each of the 24 supported banks/MFS, **When** a representative sample SMS is parsed, **Then** all applicable fields are correctly extracted (at least one passing test case per bank/MFS)

## Technical Notes

- Reuses the config from 001-define-bank-parser-configuration for per-bank regex patterns
- Account number masking must happen at extraction time — the unmasked number should never be persisted or logged anywhere downstream

## Dependencies

### Requires
- 001-define-bank-parser-configuration

### Enables
- 004-classify-transaction-type (classification needs extracted keywords/fields)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| SMS with amount but no explicit currency marker (assume local currency) | Defaults to BDT (Bangladeshi Taka) |
| SMS with ambiguous or multiple amounts in one message (e.g., balance + transaction amount) | Transaction amount is distinguished from balance via bank-specific pattern (not conflated) |
| Malformed/partial SMS (truncated by carrier) | Extraction returns whatever fields are recoverable; missing fields are null, not guessed |

## Out of Scope

- Classifying the transaction type (→ 004-classify-transaction-type)
- Sender/bank identification logic itself (→ 003-identify-bank-from-sender-with-fallback) — this story assumes the bank is already known
