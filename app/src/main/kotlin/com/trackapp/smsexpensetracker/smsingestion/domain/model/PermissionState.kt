package com.trackapp.smsexpensetracker.smsingestion.domain.model

/**
 * Live SMS access state. Never persisted directly - always re-derived from a fresh OS check
 * (see PermissionCoordinator.refreshState) so a grant/revoke made outside the app is picked up.
 */
sealed class PermissionState {
    data object NotRequested : PermissionState()
    data object Denied : PermissionState()
    data object PermanentlyDenied : PermissionState()
    data object Granted : PermissionState()
}
