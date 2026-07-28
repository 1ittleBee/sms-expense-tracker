package com.trackapp.smsexpensetracker.smsingestion.data.gateway

import com.trackapp.smsexpensetracker.smsingestion.domain.model.PermissionState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PermissionCoordinatorImplTest {

    private lateinit var coordinator: PermissionCoordinatorImpl

    @Before
    fun setUp() {
        coordinator = PermissionCoordinatorImpl()
    }

    @Test
    fun `given fresh install, when refreshed with no permission and never requested before, then state is NotRequested`() {
        coordinator.refreshState(hasSmsPermission = false, shouldShowRationale = false, hasRequestedBefore = false)

        assertEquals(PermissionState.NotRequested, coordinator.state.value)
    }

    @Test
    fun `given permission granted, when refreshed, then state is Granted`() {
        coordinator.refreshState(hasSmsPermission = true, shouldShowRationale = false, hasRequestedBefore = true)

        assertEquals(PermissionState.Granted, coordinator.state.value)
    }

    @Test
    fun `given normal denial, when result processed, then state is Denied`() {
        coordinator.onPermissionResult(granted = false, shouldShowRationaleAfterDenial = true)

        assertEquals(PermissionState.Denied, coordinator.state.value)
    }

    @Test
    fun `given permanent denial, when result processed, then state is PermanentlyDenied`() {
        coordinator.onPermissionResult(granted = false, shouldShowRationaleAfterDenial = false)

        assertEquals(PermissionState.PermanentlyDenied, coordinator.state.value)
    }

    @Test
    fun `given app restarted after permanent denial, when refreshed, then state is PermanentlyDenied not NotRequested`() {
        // Regression test for the ambiguity caught during implementation: shouldShowRationale is
        // false both for "never asked" and "permanently denied" - hasRequestedBefore disambiguates.
        coordinator.refreshState(hasSmsPermission = false, shouldShowRationale = false, hasRequestedBefore = true)

        assertEquals(PermissionState.PermanentlyDenied, coordinator.state.value)
    }

    @Test
    fun `given permission granted then revoked externally, when refreshed, then state is Denied`() {
        coordinator.refreshState(hasSmsPermission = true, shouldShowRationale = false, hasRequestedBefore = true)
        assertEquals(PermissionState.Granted, coordinator.state.value)

        coordinator.refreshState(hasSmsPermission = false, shouldShowRationale = true, hasRequestedBefore = true)

        assertEquals(PermissionState.Denied, coordinator.state.value)
    }
}
