---
id: 004-filter-non-transactional-sms
unit: 001-sms-ingestion
intent: 001-sms-transaction-capture
status: ready
priority: must
created: 2026-07-28T18:45:05Z
assigned_bolt: 002-sms-ingestion
implemented: false
---

# Story: 004-filter-non-transactional-sms

## User Story

**As a** device owner
**I want** OTPs and promotional SMS excluded from import and live detection
**So that** my transaction data isn't polluted with irrelevant messages

## Acceptance Criteria

- [ ] **Given** an SMS containing OTP-pattern keywords (e.g., "OTP", "verification code", "one time password"), **When** it passes through the filter, **Then** it is excluded and never reaches the parser
- [ ] **Given** an SMS containing promotional-pattern keywords (e.g., "offer", "cashback promo", unsubscribe footers, marketing opt-out text), **When** it passes through the filter, **Then** it is excluded and never reaches the parser
- [ ] **Given** an SMS matches a configured bank/MFS transaction keyword or sender pattern, **When** it passes through the filter, **Then** it is retained and forwarded to the parser stream
- [ ] **Given** the same filter logic, **When** applied to both historical import and live detection, **Then** behavior is identical (single shared filter implementation)

## Technical Notes

- Implement as a single, shared filter function/class used by both the import job (story 003) and the live receiver (story 005) — do not duplicate filter logic
- Keyword/pattern lists should be data-driven so they can be extended without code changes to the filter itself

## Dependencies

### Requires
- None directly, but functions as a gate used by 003 and 005

### Enables
- 003-import-historical-transaction-sms (import forwards only filtered messages)
- 005-detect-live-incoming-sms (live detection forwards only filtered messages)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| SMS contains both an OTP keyword and a bank sender ID (e.g., a bank sends an OTP for a transfer) | Excluded — OTP pattern takes precedence over sender-based inclusion |
| Promotional SMS sent from a legitimate bank sender ID (marketing message from the bank) | Excluded based on promotional content pattern, despite recognized sender |
| SMS from an unrecognized sender with transaction-like keywords in the body | Retained and forwarded — final bank/MFS attribution happens in `002-transaction-parser` (FR-6 fallback) |

## Out of Scope

- Sender/bank identification itself (→ `002-transaction-parser`)
- Machine-learning based classification (rule/keyword-based only for this intent)
