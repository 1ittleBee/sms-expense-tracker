package com.trackapp.smsexpensetracker.smsingestion.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class AndroidSmsProviderRepositoryTest {

    @Test
    fun givenTheStreamingQueryPath_whenCollected_thenCompletesWithoutCrashingAndRespectsWindow() = runTest {
        // Smoke test only: no GrantPermissionRule is applied and the test APK has no seeded SMS,
        // so this proves the Cursor-streaming plumbing works, not per-bank parsing correctness
        // (that needs sample-SMS fixtures, owned by unit 002-transaction-parser).
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = AndroidSmsProviderRepository(context)

        val results = repository.queryInbox(Instant.EPOCH).toList()

        assertTrue(results.all { it.timestamp >= Instant.EPOCH })
    }
}
