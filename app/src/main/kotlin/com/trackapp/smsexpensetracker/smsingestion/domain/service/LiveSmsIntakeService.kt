package com.trackapp.smsexpensetracker.smsingestion.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.FilterResult
import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveSmsIntakeService @Inject constructor(
    private val relevanceFilter: TransactionRelevanceFilter,
    private val smsIntakeBus: SmsIntakeBus,
) {
    /** Filters an incoming SMS and publishes it to the bus if it qualifies. Returns true if published. */
    suspend fun onSmsReceived(message: RawSmsMessage): Boolean {
        val qualifies = relevanceFilter.classify(message) is FilterResult.Include
        if (qualifies) {
            smsIntakeBus.publish(message)
        }
        return qualifies
    }
}
