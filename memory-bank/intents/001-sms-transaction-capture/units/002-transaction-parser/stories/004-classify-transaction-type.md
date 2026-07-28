---
id: 004-classify-transaction-type
unit: 002-transaction-parser
intent: 001-sms-transaction-capture
status: ready
priority: must
created: 2026-07-28T18:45:05Z
assigned_bolt: 004-transaction-parser
implemented: false
---

# Story: 004-classify-transaction-type

## User Story

**As a** device owner
**I want** each transaction classified as Expense, Income, Transfer, ATM Withdrawal, Card Purchase, or Refund
**So that** I can understand what kind of transaction occurred at a glance

## Acceptance Criteria

- [ ] **Given** a parsed SMS with withdrawal/ATM keywords ("withdrawn", "ATM Withdrawal", "Cash Withdrawal", "Cash Out"), **When** classified, **Then** transaction type is `ATM Withdrawal`
- [ ] **Given** a parsed SMS with purchase/payment keywords ("Purchase", "Payment", "Card Purchase"), **When** classified, **Then** transaction type is `Card Purchase`
- [ ] **Given** a parsed SMS with credit/salary keywords ("Salary Credited", "Deposit", "Credit"), **When** classified, **Then** transaction type is `Income`
- [ ] **Given** a parsed SMS with transfer keywords ("Transfer"), **When** classified, **Then** transaction type is `Transfer`
- [ ] **Given** a parsed SMS with refund keywords ("Refund"), **When** classified, **Then** transaction type is `Refund`
- [ ] **Given** a parsed SMS with generic debit keywords not matching a more specific type ("Debit"), **When** classified, **Then** transaction type is `Expense`
- [ ] **Given** any successfully field-extracted transaction, **When** classification completes, **Then** the transaction type is never null

## Technical Notes

- Classification keyword rules are part of each bank's config (per 001-define-bank-parser-configuration) since exact wording varies by bank/MFS
- Define and document a deterministic precedence order for overlapping keywords (e.g., "Cash Withdrawal" should resolve to ATM Withdrawal, not generic Expense)

## Dependencies

### Requires
- 002-parse-transaction-amount-and-metadata
- 003-identify-bank-from-sender-with-fallback

### Enables
- None further within this unit

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| SMS matches keywords for two types (e.g., "Cash Withdrawal Payment") | Documented precedence order resolves to a single, deterministic type |
| SMS from a bank/MFS with wording not covered by any keyword rule | Falls back to a documented default type (`Expense` for debit-like language, `Income` for credit-like language) rather than null |

## Out of Scope

- Sub-categorization (e.g., "Groceries", "Utilities") — that's category assignment in a later intent, distinct from transaction type
