package com.trackapp.smsexpensetracker.smsingestion.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cross-unit seam: both the historical import and live detection pipelines publish qualified
 * messages here. Unit 002-transaction-parser subscribes to [qualifiedMessages] once it exists -
 * this bolt does not depend on that unit being built.
 */
@Singleton
class SmsIntakeBus @Inject constructor() {

    private val _qualifiedMessages = MutableSharedFlow<RawSmsMessage>(extraBufferCapacity = 64)
    val qualifiedMessages: SharedFlow<RawSmsMessage> = _qualifiedMessages.asSharedFlow()

    suspend fun publish(message: RawSmsMessage) {
        _qualifiedMessages.emit(message)
    }
}
