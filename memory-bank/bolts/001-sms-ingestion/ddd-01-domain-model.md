---
unit: 001-sms-ingestion
bolt: 001-sms-ingestion
stage: model
status: complete
updated: 2026-07-28T19:11:43Z
---

# Static Model - SMS Ingestion (Permission Flow)

## Bounded Context

**Permission Access Management**: Governs whether the app currently has OS-granted access to read SMS, and the user-facing flow (rationale → system dialog → grant/deny → retry/Settings deep-link) required to obtain that access. This bounded context owns no persisted data — it wraps live Android permission state and the interaction flow around it. It is the gate that everything else in the capture pipeline (import, live detection) waits behind.

## Domain Entities

_None._ This bolt has no persisted entities — permission state is transient OS-level state queried live from the Android platform, not stored by the app. (Persistence begins downstream, in unit `003-transaction-persistence`.)

## Value Objects

| Value Object | Properties | Constraints |
|--------------|------------|-------------|
| `PermissionState` | `status: Granted \| Denied \| PermanentlyDenied \| NotRequested` | Immutable; derived fresh from `ContextCompat.checkSelfPermission()` + `shouldShowRequestPermissionRationale()` each time it's read — never cached stale across app resume |
| `RationaleCopy` | `title: String`, `body: String` | Must explicitly state: read-only access, transaction SMS only, no data leaves device (per intent's security constraint) |

## Aggregates

_None._ No entity requires transactional consistency boundaries in this bolt — the domain service below coordinates the flow without an aggregate root.

## Domain Events

| Event | Trigger | Payload |
|-------|---------|---------|
| `PermissionGranted` | User grants `READ_SMS`/`RECEIVE_SMS` (via dialog or after returning from Settings) | `grantedAt: Instant` |
| `PermissionDenied` | User denies the system dialog | `isPermanent: Boolean` (true if "don't ask again" was selected) |
| `PermissionRetryRequested` | User taps "Retry" on the denial screen | `previousState: PermissionState` |

**Consumers**: `PermissionGranted` is the trigger that unblocks bolt `002-sms-ingestion`'s import job and live-detection receiver registration (cross-bolt dependency, not implemented in this bolt).

## Domain Services

| Service | Operations | Dependencies |
|---------|------------|--------------|
| `PermissionCoordinator` | `checkCurrentState(): PermissionState`, `requestPermission()`, `handleDenialResult(permanent: Boolean)`, `retry()` | Android `ActivityResultContracts.RequestMultiplePermissions`, `shouldShowRequestPermissionRationale()`, system Settings intent (`ACTION_APPLICATION_DETAILS_SETTINGS`) |

**Business rules enforced by `PermissionCoordinator`**:
- Never requests `SEND_SMS` — only `READ_SMS` and `RECEIVE_SMS` are ever passed to the request call
- Distinguishes "can retry with in-app dialog" (`shouldShowRequestPermissionRationale() == true`) from "must deep-link to Settings" (permanent denial) — never re-shows a dialog Android will silently suppress
- Re-evaluates `PermissionState` on every `onResume()` so a grant made from Settings is picked up automatically without an extra user tap

## Repository Interfaces

_None._ No repository needed in this bolt — permission state is queried live from the Android OS permission APIs, not persisted by the app.

## Ubiquitous Language

| Term | Definition |
|------|------------|
| **Rationale** | The explanatory screen shown before the system permission dialog, stating why SMS access is needed |
| **Grant** | OS-level confirmation that `READ_SMS`/`RECEIVE_SMS` access is allowed |
| **Denial** | User declines the system permission dialog; may be normal (retryable) or permanent |
| **Permanent Denial** | Denial state where the user selected "Don't ask again" — Android will no longer show the dialog; the only path forward is the system Settings screen |
| **Retry** | User-initiated re-attempt to obtain permission after a (non-permanent) denial |
