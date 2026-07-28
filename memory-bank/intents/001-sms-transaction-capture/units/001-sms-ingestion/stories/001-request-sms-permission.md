---
id: 001-request-sms-permission
unit: 001-sms-ingestion
intent: 001-sms-transaction-capture
status: complete
priority: must
created: '2026-07-28T18:45:05Z'
assigned_bolt: 001-sms-ingestion
implemented: true
---

# Story: 001-request-sms-permission

## User Story

**As a** device owner installing the app
**I want** to see a clear explanation of why SMS access is needed before the system permission dialog appears
**So that** I understand the app only reads transaction SMS and can make an informed decision

## Acceptance Criteria

- [ ] **Given** the app is launched for the first time, **When** the permission screen is reached, **Then** a rationale screen is shown explaining that the app reads (never sends) transaction SMS, before the system `READ_SMS`/`RECEIVE_SMS` dialog appears
- [ ] **Given** the rationale screen is shown, **When** the user taps "Continue"/"Allow", **Then** the system permission dialog is triggered requesting `READ_SMS` and `RECEIVE_SMS` only
- [ ] **Given** the user grants permission, **When** the grant is confirmed, **Then** the app proceeds to trigger historical import (story 003) and registers live detection (story 005)
- [ ] **Given** the manifest/runtime permission requests, **When** inspected, **Then** `SEND_SMS` is never declared or requested

## Technical Notes

- Use the standard Android runtime permission APIs (`ActivityResultContracts.RequestMultiplePermissions` or equivalent)
- Rationale copy should explicitly state: read-only, transaction SMS only, no data leaves the device

## Dependencies

### Requires
- None (first story)

### Enables
- 002-handle-permission-denial-retry
- 003-import-historical-transaction-sms
- 005-detect-live-incoming-sms

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| User has already granted permission in a prior session | Rationale/dialog skipped; pipeline proceeds directly |
| Device has no SMS capability (e.g., tablet, some emulators) | Rationale explains SMS capture is unavailable; app remains usable in a degraded/manual-entry state (manual entry itself is out of scope for this intent) |

## Out of Scope

- Full onboarding/splash screen visual design (handled by later UI-focused intent)
- Manual transaction entry fallback UI
