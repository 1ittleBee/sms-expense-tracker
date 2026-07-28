package com.trackapp.smsexpensetracker.smsingestion.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.smsingestion.domain.model.SmsSource
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class SmsIntakeBusTest {

    @Test
    fun `given a published message, when a subscriber is collecting, then it receives that message`() = runTest {
        val bus = SmsIntakeBus()
        val message = RawSmsMessage(
            sender = "DBBL",
            body = "Tk 100 debited",
            timestamp = Instant.now(),
            source = SmsSource.Live,
        )

        val received = async { bus.qualifiedMessages.first() }
        runCurrent() // ensure the collector has subscribed before we publish

        bus.publish(message)

        assertEquals(message, received.await())
    }
}
