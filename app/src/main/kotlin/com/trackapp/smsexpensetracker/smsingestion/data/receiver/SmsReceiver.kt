package com.trackapp.smsexpensetracker.smsingestion.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.trackapp.smsexpensetracker.smsingestion.di.ApplicationScope
import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.smsingestion.domain.model.SmsSource
import com.trackapp.smsexpensetracker.smsingestion.domain.service.LiveSmsIntakeService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/**
 * Read-only observer of incoming SMS. Never calls abortBroadcast() and never writes to the SMS
 * provider - it only forwards qualifying messages into [LiveSmsIntakeService].
 */
@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var liveSmsIntakeService: LiveSmsIntakeService

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        // Extras must be read synchronously here - they are unavailable after onReceive returns.
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
        val rawMessages = messages.map { sms ->
            RawSmsMessage(
                sender = sms.originatingAddress.orEmpty(),
                body = sms.messageBody.orEmpty(),
                timestamp = Instant.ofEpochMilli(sms.timestampMillis),
                source = SmsSource.Live,
            )
        }

        val pendingResult = goAsync()
        applicationScope.launch {
            try {
                rawMessages.forEach { liveSmsIntakeService.onSmsReceived(it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
