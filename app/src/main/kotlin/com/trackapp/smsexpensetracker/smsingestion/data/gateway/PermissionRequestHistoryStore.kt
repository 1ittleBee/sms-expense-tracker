package com.trackapp.smsexpensetracker.smsingestion.data.gateway

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val HAS_REQUESTED_SMS_PERMISSION = booleanPreferencesKey("has_requested_sms_permission")

/**
 * Tracks whether the system SMS permission dialog has ever been shown to the user.
 *
 * This is the only way to distinguish "permission never requested" from "permanently denied" on a
 * fresh process (both report `shouldShowRequestPermissionRationale() == false`).
 */
@Singleton
class PermissionRequestHistoryStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val hasRequestedBefore: Flow<Boolean> = dataStore.data.map { it[HAS_REQUESTED_SMS_PERMISSION] ?: false }

    suspend fun markRequested() {
        dataStore.edit { it[HAS_REQUESTED_SMS_PERMISSION] = true }
    }
}
