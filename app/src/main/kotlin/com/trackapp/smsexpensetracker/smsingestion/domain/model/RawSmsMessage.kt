package com.trackapp.smsexpensetracker.smsingestion.domain.model

import java.time.Instant

data class RawSmsMessage(
    val sender: String,
    val body: String,
    val timestamp: Instant,
    val source: SmsSource,
)
