package com.trackapp.smsexpensetracker.smsingestion.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.smsingestion.domain.model.SmsSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class LiveSmsIntakeServiceTest {

    private val service = LiveSmsIntakeService(TransactionRelevanceFilter(), SmsIntakeBus())

    @Test
    fun `given a transaction-relevant message, when received, then it qualifies`() = runTest {
        val message = RawSmsMessage(
            sender = "BKASH",
            body = "You have received Tk 500. Cash In successful",
            timestamp = Instant.now(),
            source = SmsSource.Live,
        )

        assertTrue(service.onSmsReceived(message))
    }

    @Test
    fun `given an OTP message, when received, then it does not qualify`() = runTest {
        val message = RawSmsMessage(
            sender = "BKASH",
            body = "Your OTP is 1234, do not share this code",
            timestamp = Instant.now(),
            source = SmsSource.Live,
        )

        assertFalse(service.onSmsReceived(message))
    }
}
