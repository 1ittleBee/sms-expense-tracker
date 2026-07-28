---
id: 001-define-bank-parser-configuration
unit: 002-transaction-parser
intent: 001-sms-transaction-capture
status: ready
priority: must
created: 2026-07-28T18:45:05Z
assigned_bolt: 003-transaction-parser
implemented: false
---

# Story: 001-define-bank-parser-configuration

## User Story

**As a** developer maintaining this app
**I want** a data-driven configuration format describing each bank/MFS's SMS patterns
**So that** I can add or adjust a bank's parsing rules without touching the parser engine code

## Acceptance Criteria

- [ ] **Given** the parser engine, **When** inspected, **Then** it reads bank/MFS rules from a config structure (sender IDs, body keywords, field regex patterns, type-classification keywords) rather than per-sender `if/when` branches
- [ ] **Given** a config entry exists for a bank, **When** the engine loads, **Then** that bank's rules are available to `IdentifyBank`, `ExtractFields`, and `ClassifyTransactionType` without any other code changes
- [ ] **Given** all 20 banks (DBBL, BRAC, City, Eastern, Islami, Dutch-Bangla, Standard Chartered, HSBC, Prime, IFIC, NCC, Bank Asia, Mutual Trust, EBL, UCB, Sonali, Janata, Agrani, Rupali) and 4 MFS providers (bKash, Nagad, Rocket, Upay), **When** the config is loaded, **Then** each has a corresponding config entry
- [ ] **Given** a new bank needs to be added, **When** a developer adds a config entry only, **Then** no changes to the engine's core classes are required

## Technical Notes

- Config can be Kotlin data classes bundled at compile time, or externalized JSON/YAML loaded at startup — decide during Construction's Technical Design stage
- Structure should separate: sender identification rules, field extraction regex per field, and type-classification keyword rules

## Dependencies

### Requires
- None (first story in this unit)

### Enables
- 002-parse-transaction-amount-and-metadata
- 003-identify-bank-from-sender-with-fallback
- 004-classify-transaction-type

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Two banks share a similar SMS format | Each still has its own distinct config entry; no cross-bank pattern leakage |
| A bank's SMS format has multiple variants (e.g., debit vs. deposit templates) | Config for that bank supports multiple pattern entries, all mapped to the same bank identity |

## Out of Scope

- Remote/dynamic config updates (config ships with the app for this intent)
- Actual field-extraction regex authoring (→ 002-parse-transaction-amount-and-metadata)
