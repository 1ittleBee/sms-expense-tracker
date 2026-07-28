package com.trackapp.smsexpensetracker.smsingestion.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackapp.smsexpensetracker.smsingestion.data.gateway.PermissionRequestHistoryStore
import com.trackapp.smsexpensetracker.smsingestion.domain.model.PermissionState
import com.trackapp.smsexpensetracker.smsingestion.domain.service.PermissionCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val permissionCoordinator: PermissionCoordinator,
    private val historyStore: PermissionRequestHistoryStore,
) : ViewModel() {

    val state: StateFlow<PermissionState> = permissionCoordinator.state

    /** Call when the permission screen becomes visible (including after returning from Settings). */
    fun onScreenResumed(hasSmsPermission: Boolean, shouldShowRationale: Boolean) {
        viewModelScope.launch {
            val hasRequestedBefore = historyStore.hasRequestedBefore.first()
            permissionCoordinator.refreshState(hasSmsPermission, shouldShowRationale, hasRequestedBefore)
        }
    }

    /** Call with the result of the system permission dialog. */
    fun onPermissionResult(granted: Boolean, shouldShowRationaleAfterDenial: Boolean) {
        viewModelScope.launch {
            historyStore.markRequested()
            permissionCoordinator.onPermissionResult(granted, shouldShowRationaleAfterDenial)
        }
    }
}
