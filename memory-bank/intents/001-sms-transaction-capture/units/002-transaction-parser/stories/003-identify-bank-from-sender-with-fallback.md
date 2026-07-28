---
id: 003-identify-bank-from-sender-with-fallback
unit: 002-transaction-parser
intent: 001-sms-transaction-capture
status: ready
priority: should
created: 2026-07-28T18:45:05Z
assigned_bolt: 004-transaction-parser
implemented: false
---

# Story: 003-identify-bank-from-sender-with-fallback

## User Story

**As a** device owner
**I want** the app to correctly identify which bank/MFS sent a message even when the sender ID is generic
**So that** my transactions are attributed to the right bank

## Acceptance Criteria

- [ ] **Given** an SMS from a known, specific sender ID (e.g., `DBBL`, `bKash`), **When** identified, **Then** it maps directly to that bank's config entry
- [ ] **Given** an SMS from a generic/shared sender ID (e.g., a generic "AD-BANK" short code), **When** the sender ID alone doesn't resolve, **Then** the parser falls back to matching bank name/keywords within the message body
- [ ] **Given** neither sender ID nor body keywords resolve to a known bank, **When** identification completes, **Then** the SMS is classified as "unknown bank" rather than misattributed
- [ ] **Given** a body-keyword fallback match, **When** identification completes, **Then** the same field-extraction and classification rules apply as for a direct sender-ID match

## Technical Notes

- Sender-ID lookup should be tried first (cheap, precise); body-keyword fallback only runs when sender-ID lookup fails
- Body-keyword fallback should use bank name variants (e.g., "Dutch-Bangla", "DBBL") from the same config entry used for sender-ID matching

## Dependencies

### Requires
- 001-define-bank-parser-configuration
- 002-parse-transaction-amount-and-metadata

### Enables
- 004-classify-transaction-type (needs to know which bank's classification rules to apply)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Sender ID resolves to Bank A but body text mentions Bank B (e.g., a transfer confirmation referencing another bank) | Sender ID takes precedence over body content when both are present |
| Body contains keywords for two different banks (rare edge case) | First/most specific match wins; documented as a known limitation rather than silently misattributing |

## Out of Scope

- Building a comprehensive NLP-based sender classifier (rule-based fallback only for this intent)
