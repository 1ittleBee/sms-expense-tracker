package com.trackapp.smsexpensetracker.smsingestion.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.FilterResult
import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.smsingestion.domain.model.SmsSource
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class TransactionRelevanceFilterTest {

    private val filter = TransactionRelevanceFilter()

    private fun message(body: String, sender: String = "UNKNOWN") = RawSmsMessage(
        sender = sender,
        body = body,
        timestamp = Instant.now(),
        source = SmsSource.Import,
    )

    @Test
    fun `given an OTP message, when classified, then result is ExcludeOtp`() {
        val result = filter.classify(message("Your OTP is 4321, do not share this code with anyone", sender = "DBBL"))

        assertEquals(FilterResult.ExcludeOtp, result)
    }

    @Test
    fun `given a promotional message, when classified, then result is ExcludePromotional`() {
        val result = filter.classify(message("Get 50% discount this weekend, unsubscribe anytime", sender = "DBBL"))

        assertEquals(FilterResult.ExcludePromotional, result)
    }

    @Test
    fun `given a known bank sender with a transaction keyword, when classified, then result is Include`() {
        val result = filter.classify(message("Tk 500 withdrawn from your account", sender = "DBBL"))

        assertEquals(FilterResult.Include, result)
    }

    @Test
    fun `given an unrecognized sender with a transaction keyword in the body, when classified, then result is Include`() {
        val result = filter.classify(message("Cash Withdrawal of BDT 1000 completed", sender = "AD-BANK"))

        assertEquals(FilterResult.Include, result)
    }

    @Test
    fun `given an unrelated personal message, when classified, then result is ExcludeNotTransactional`() {
        val result = filter.classify(message("Hey, are we still meeting today?", sender = "+8801700000000"))

        assertEquals(FilterResult.ExcludeNotTransactional, result)
    }

    @Test
    fun `given a message that is both OTP-worded and from a known bank sender, when classified, then OTP takes precedence`() {
        val result = filter.classify(message("Your OTP for the transfer is 9999, do not share this code", sender = "DBBL"))

        assertEquals(FilterResult.ExcludeOtp, result)
    }
}
