package com.trackapp.smsexpensetracker.transactionparser.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.smsingestion.domain.model.SmsSource
import com.trackapp.smsexpensetracker.transactionparser.domain.model.ParsingOutcome
import com.trackapp.smsexpensetracker.transactionparser.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class TransactionParserTest {

    private val registry = BankParserConfigRegistry()
    private val parser = TransactionParser(
        senderResolver = SenderResolver(registry),
        fieldExtractor = TransactionFieldExtractor(AccountNumberMasker()),
        classifier = TransactionTypeClassifier(),
    )

    private fun message(sender: String, body: String) = RawSmsMessage(
        sender = sender,
        body = body,
        timestamp = Instant.parse("2026-07-29T10:00:00Z"),
        source = SmsSource.Import,
    )

    @Test
    fun `given a fully valid transaction SMS, when parsed, then outcome is Classified with correct fields`() {
        val outcome = parser.parse(message("DBBL", "Cash Withdrawal Tk 500 from your account"))

        val classified = outcome as ParsingOutcome.Classified
        assertEquals(BigDecimal("500"), classified.transaction.amount)
        assertEquals("DBBL", classified.transaction.bankId)
        assertEquals(TransactionType.AtmWithdrawal, classified.transaction.transactionType)
    }

    @Test
    fun `given a known sender but a body with no amount pattern, when parsed, then outcome is FlaggedForReview`() {
        val outcome = parser.parse(message("DBBL", "Your account statement is now ready to view"))

        val flagged = outcome as ParsingOutcome.FlaggedForReview
        assertEquals("DBBL", flagged.flagged.sender)
        assertEquals("Your account statement is now ready to view", flagged.flagged.rawBody)
    }

    @Test
    fun `given a completely unrecognized sender and body, when parsed, then outcome is SenderUnknown`() {
        val outcome = parser.parse(message("+8801700000000", "Hey, are we still meeting today?"))

        assertTrue(outcome is ParsingOutcome.SenderUnknown)
    }

    @Test
    fun `given a fallback-resolved sender with a valid amount, when parsed, then outcome is still Classified`() {
        val outcome = parser.parse(message("AD-BANK", "BKASH: Cash In Tk 300 received"))

        val classified = outcome as ParsingOutcome.Classified
        assertEquals("BKASH", classified.transaction.bankId)
        assertEquals(BigDecimal("300"), classified.transaction.amount)
    }
}
