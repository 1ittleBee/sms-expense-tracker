package com.trackapp.smsexpensetracker.smsingestion.data.gateway

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmsPermissionSpecTest {

    @Test
    fun givenPermissionNotGrantedToTestApk_whenChecked_thenHasSmsPermissionIsFalse() {
        // No GrantPermissionRule is applied, so this verifies the check reflects the OS's real,
        // default-denied state for a fresh test APK - not just that the function returns a value.
        val context = ApplicationProvider.getApplicationContext<Context>()

        assertFalse(hasSmsPermission(context))
    }
}
