package com.trackapp.smsexpensetracker.smsingestion.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.PermissionState
import kotlinx.coroutines.flow.StateFlow

/**
 * Pure domain logic for interpreting SMS permission status. Holds no Android Context/Activity
 * reference - all OS-level facts are supplied as booleans by the caller (infrastructure/presentation
 * layer), which keeps this fully unit-testable without Robolectric or instrumentation.
 */
interface PermissionCoordinator {
    val state: StateFlow<PermissionState>

    /**
     * Re-derive state from a fresh OS-level check (e.g. on screen resume).
     *
     * [hasRequestedBefore] is required to distinguish "never asked" from "permanently denied":
     * both report `shouldShowRationale = false`, and that ambiguity only resolves once we know
     * whether the system dialog has ever actually been shown.
     */
    fun refreshState(hasSmsPermission: Boolean, shouldShowRationale: Boolean, hasRequestedBefore: Boolean)

    /** Interpret the result of a system permission dialog just shown to the user. */
    fun onPermissionResult(granted: Boolean, shouldShowRationaleAfterDenial: Boolean)
}
