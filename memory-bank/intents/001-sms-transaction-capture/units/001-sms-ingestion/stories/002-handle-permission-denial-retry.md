---
id: 002-handle-permission-denial-retry
unit: 001-sms-ingestion
intent: 001-sms-transaction-capture
status: complete
priority: must
created: '2026-07-28T18:45:05Z'
assigned_bolt: 001-sms-ingestion
implemented: true
---

# Story: 002-handle-permission-denial-retry

## User Story

**As a** device owner who denied SMS permission
**I want** to understand what functionality I'm missing and be able to retry granting permission
**So that** I'm not permanently locked out of the app's core feature by a single accidental tap

## Acceptance Criteria

- [ ] **Given** the user denies the permission dialog, **When** the denial is registered, **Then** a screen explains that transaction capture is unavailable without SMS access and offers a "Retry" action
- [ ] **Given** the user taps "Retry" after a normal denial, **When** the retry is triggered, **Then** the system permission dialog is shown again
- [ ] **Given** the user has denied permission with "Don't ask again" (permanent denial), **When** retry is attempted, **Then** the app detects the permanent-denial state and deep-links to the app's system Settings page instead of re-showing a dialog that Android will suppress
- [ ] **Given** the user grants permission from Settings and returns to the app, **When** the app resumes, **Then** it detects the newly granted permission and proceeds automatically (no extra tap required)

## Technical Notes

- Use `shouldShowRequestPermissionRationale()` to distinguish "can retry with dialog" vs "must go to Settings"
- Recheck permission state in `onResume()` after returning from Settings

## Dependencies

### Requires
- 001-request-sms-permission

### Enables
- 003-import-historical-transaction-sms
- 005-detect-live-incoming-sms

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| User denies repeatedly without checking "don't ask again" | Retry re-shows the rationale + dialog each time |
| User revokes permission later from system Settings while app is running | Next foreground/resume detects revocation and returns to the denied state, pausing import/live detection |

## Out of Scope

- Any alternative data-entry path when permission is permanently refused
