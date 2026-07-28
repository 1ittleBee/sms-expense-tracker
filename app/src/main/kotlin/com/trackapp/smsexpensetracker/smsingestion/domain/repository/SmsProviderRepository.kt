package com.trackapp.smsexpensetracker.smsingestion.domain.repository

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/** Read-only access to the device's SMS inbox. Never writes, never sends. */
interface SmsProviderRepository {
    fun queryInbox(since: Instant): Flow<RawSmsMessage>
}
