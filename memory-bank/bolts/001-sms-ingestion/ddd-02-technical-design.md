---
unit: 001-sms-ingestion
bolt: 001-sms-ingestion
stage: design
status: complete
updated: 2026-07-28T19:12:54Z
---

# Technical Design - SMS Ingestion (Permission Flow)

## Architecture Pattern

Clean Architecture (Presentation → Domain → Infrastructure), MVVM in the presentation layer — matches `standards/coding-standards.md`'s package-per-layer convention. No data layer is needed in this bolt (no persistence in scope), so the layering collapses to three: Presentation, Domain, Infrastructure (the latter standing in for `data/` since it wraps a platform API rather than a database).

## Layer Structure

```text
┌─────────────────────────────────────┐
│  Presentation                       │  PermissionScreen (Compose), PermissionViewModel
├─────────────────────────────────────┤
│  Domain                             │  PermissionCoordinator, PermissionState, PermissionUiState mapping, domain events
├─────────────────────────────────────┤
│  Infrastructure                     │  AndroidPermissionGateway (wraps ActivityResultContracts, ContextCompat, Settings intent)
└─────────────────────────────────────┘
```

**Package layout** (per `coding-standards.md`):
```text
smsingestion/
  domain/
    model/PermissionState.kt
    model/RationaleCopy.kt
    service/PermissionCoordinator.kt      # interface
  data/
    gateway/AndroidPermissionGateway.kt    # PermissionCoordinator implementation
  presentation/
    PermissionViewModel.kt
    PermissionScreen.kt
```

## Public Interface

_No REST/API surface (native app, no backend). Instead: the cross-layer contracts this bolt exposes._

| Interface | Exposed By | Consumed By |
|-----------|------------|-------------|
| `PermissionCoordinator.checkCurrentState(): PermissionState` | Domain (interface), Infrastructure (impl) | `PermissionViewModel` |
| `PermissionCoordinator.requestPermission()` (suspend, launches system dialog via composed `ActivityResultLauncher`) | Domain/Infrastructure | `PermissionViewModel` |
| `PermissionCoordinator.retry()` | Domain/Infrastructure | `PermissionViewModel` |
| `PermissionViewModel.uiState: StateFlow<PermissionUiState>` | Presentation | `PermissionScreen` (Compose) |
| `PermissionEventBus` (emits `PermissionGranted`) | Domain | Bolt `002-sms-ingestion`'s import job / receiver registration (cross-bolt — interface only, no implementation here) |

## Data Persistence

_None._ Permission state is never written to Room or DataStore in this bolt — `PermissionCoordinator.checkCurrentState()` always reads live from the Android OS (`ContextCompat.checkSelfPermission`, `shouldShowRequestPermissionRationale`). This avoids the class of bugs where a cached "granted" flag goes stale after the user revokes permission from system Settings.

## Security Design

| Concern | Approach |
|---------|----------|
| Authentication | N/A — no user accounts in this app |
| Authorization | Android runtime permission model — only `READ_SMS` and `RECEIVE_SMS` are ever requested; `SEND_SMS` is never declared in the manifest or requested at runtime |
| Data Encryption | N/A — no data persisted by this bolt |

## NFR Implementation

| Requirement | Design Approach |
|-------------|-----------------|
| Responsiveness | Permission check (`checkCurrentState`) is a cheap synchronous OS call — no background threading needed in this bolt |
| Reliability | `checkCurrentState()` is re-invoked in `PermissionViewModel`'s `onResume`-equivalent lifecycle hook so a grant made from system Settings is picked up without requiring an extra in-app action (satisfies story 002's acceptance criteria) |
| Testability | `PermissionCoordinator` defined as an interface so `PermissionViewModel` can be unit-tested with a fake implementation, independent of real Android permission APIs; real behavior verified via Espresso instrumented tests |

## Error Handling

| Error Type | Sealed Result Case | Response |
|------------|--------------------|----------|
| Permission denied (retryable) | `PermissionState.Denied` | UI shows rationale + retry button, re-triggers dialog |
| Permission denied permanently | `PermissionState.PermanentlyDenied` | UI shows explanation + "Open Settings" button, launches `ACTION_APPLICATION_DETAILS_SETTINGS` |
| Not yet requested | `PermissionState.NotRequested` | UI shows initial rationale screen |

(Per `coding-standards.md`'s error-handling pattern: this is expected-outcome branching via a sealed value object, not exception-based.)

## External Dependencies

| Service | Purpose | Integration |
|---------|---------|-------------|
| Android Runtime Permissions API | Request/check `READ_SMS`, `RECEIVE_SMS` | `ActivityResultContracts.RequestMultiplePermissions`, `ContextCompat.checkSelfPermission` |
| Android Settings | Deep-link for permanently-denied recovery | `Intent(ACTION_APPLICATION_DETAILS_SETTINGS)` |
