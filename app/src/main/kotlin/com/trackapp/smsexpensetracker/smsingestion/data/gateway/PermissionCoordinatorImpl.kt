package com.trackapp.smsexpensetracker.smsingestion.data.gateway

import com.trackapp.smsexpensetracker.smsingestion.domain.model.PermissionState
import com.trackapp.smsexpensetracker.smsingestion.domain.service.PermissionCoordinator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionCoordinatorImpl @Inject constructor() : PermissionCoordinator {

    private val _state = MutableStateFlow<PermissionState>(PermissionState.NotRequested)
    override val state: StateFlow<PermissionState> = _state.asStateFlow()

    override fun refreshState(hasSmsPermission: Boolean, shouldShowRationale: Boolean, hasRequestedBefore: Boolean) {
        _state.value = when {
            hasSmsPermission -> PermissionState.Granted
            shouldShowRationale -> PermissionState.Denied
            hasRequestedBefore -> PermissionState.PermanentlyDenied
            else -> PermissionState.NotRequested
        }
    }

    override fun onPermissionResult(granted: Boolean, shouldShowRationaleAfterDenial: Boolean) {
        _state.value = when {
            granted -> PermissionState.Granted
            shouldShowRationaleAfterDenial -> PermissionState.Denied
            else -> PermissionState.PermanentlyDenied
        }
    }
}
