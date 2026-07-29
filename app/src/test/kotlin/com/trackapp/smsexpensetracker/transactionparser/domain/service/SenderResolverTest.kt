package com.trackapp.smsexpensetracker.transactionparser.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.smsingestion.domain.model.SmsSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class SenderResolverTest {

    private val resolver = SenderResolver(BankParserConfigRegistry())

    private fun message(sender: String, body: String) = RawSmsMessage(
        sender = sender,
        body = body,
        timestamp = Instant.now(),
        source = SmsSource.Import,
    )

    @Test
    fun `given a known sender ID, when resolved, then it matches directly without needing fallback`() {
        val config = resolver.resolve(message("DBBL", "Tk 500 withdrawn"))

        assertEquals("DBBL", config?.bankId)
    }

    @Test
    fun `given an unrecognized sender ID but the body mentions a bank ID, when resolved, then fallback matches`() {
        val config = resolver.resolve(message("AD-BANK", "DBBL: Tk 500 withdrawn from your account"))

        assertEquals("DBBL", config?.bankId)
    }

    @Test
    fun `given an unrecognized sender ID with no bank mentioned in the body, when resolved, then null is returned`() {
        val config = resolver.resolve(message("+8801700000000", "Hey, are we still meeting today?"))

        assertNull(config)
    }

    @Test
    fun `given a known sender ID whose body happens to mention a different bank, when resolved, then sender ID still wins`() {
        val config = resolver.resolve(message("DBBL", "Transfer received from BRAC account"))

        assertEquals("DBBL", config?.bankId)
    }
}
