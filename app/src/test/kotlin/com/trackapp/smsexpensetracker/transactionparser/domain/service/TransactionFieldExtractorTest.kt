package com.trackapp.smsexpensetracker.transactionparser.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.smsingestion.domain.model.SmsSource
import com.trackapp.smsexpensetracker.transactionparser.data.config.BankParserConfigs
import com.trackapp.smsexpensetracker.transactionparser.domain.model.ExtractionResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneOffset

class TransactionFieldExtractorTest {

    private val extractor = TransactionFieldExtractor(AccountNumberMasker())
    private val registry = BankParserConfigRegistry()

    private fun message(body: String, sender: String, timestamp: Instant = Instant.parse("2026-07-28T10:15:00Z")) =
        RawSmsMessage(sender = sender, body = body, timestamp = timestamp, source = SmsSource.Import)

    private fun configFor(sender: String) = registry.findBySenderId(sender) ?: error("no config for $sender")

    @Test
    fun `given every bundled bank config, when a generic transaction SMS is extracted, then it matches (FR-5 per-bank sample coverage)`() {
        BankParserConfigs.all.forEach { config ->
            val sms = message("Tk 500 withdrawn from your account", config.senderIds.first())

            val result = extractor.extract(sms, config)

            assertTrue("Expected Matched for bank ${config.bankId}", result is ExtractionResult.Matched)
        }
    }

    @Test
    fun `given "Tk 500 withdrawn", when extracted, then amount currency and bank are correct`() {
        val config = configFor("DBBL")

        val result = extractor.extract(message("Tk 500 withdrawn from your account", "DBBL"), config)

        val transaction = (result as ExtractionResult.Matched).transaction
        assertEquals(BigDecimal("500"), transaction.amount)
        assertEquals("BDT", transaction.currency)
        assertEquals("DBBL", transaction.bankId)
    }

    @Test
    fun `given "Debit BDT 1200", when extracted, then amount is correct`() {
        val config = configFor("BRAC")

        val result = extractor.extract(message("Debit BDT 1200 from your account", "BRAC"), config)

        val transaction = (result as ExtractionResult.Matched).transaction
        assertEquals(BigDecimal("1200"), transaction.amount)
    }

    @Test
    fun `given "Purchase BDT 900 at STARBUCKS", when extracted, then merchant is captured`() {
        val config = configFor("CITYBANK")

        val result = extractor.extract(message("Purchase BDT 900 at STARBUCKS", "CITYBANK"), config)

        val transaction = (result as ExtractionResult.Matched).transaction
        assertEquals(BigDecimal("900"), transaction.amount)
        assertEquals("STARBUCKS", transaction.merchant)
    }

    @Test
    fun `given "ATM Withdrawal Tk 1000" with no merchant phrasing, when extracted, then merchant is null`() {
        val config = configFor("DBBL")

        val result = extractor.extract(message("ATM Withdrawal Tk 1000", "DBBL"), config)

        val transaction = (result as ExtractionResult.Matched).transaction
        assertEquals(BigDecimal("1000"), transaction.amount)
        assertNull("merchant should never be guessed when absent", transaction.merchant)
    }

    @Test
    fun `given "BDT 2500 deposited to account ending 4321", when extracted, then only the last digits are masked`() {
        val config = configFor("SONALI")

        val result = extractor.extract(message("BDT 2500 deposited to account ending 4321", "SONALI"), config)

        val transaction = (result as ExtractionResult.Matched).transaction
        assertEquals(BigDecimal("2500"), transaction.amount)
        assertEquals("****4321", transaction.maskedAccountNumber)
    }

    @Test
    fun `given "Salary Credited Tk 35000", when extracted, then amount is correct`() {
        val config = configFor("BKASH")

        val result = extractor.extract(message("Salary Credited Tk 35000", "BKASH"), config)

        val transaction = (result as ExtractionResult.Matched).transaction
        assertEquals(BigDecimal("35000"), transaction.amount)
    }

    @Test
    fun `given "Payment Tk 12,450 processed", when extracted, then comma thousands separator is handled`() {
        val config = configFor("EBL")

        val result = extractor.extract(message("Payment Tk 12,450 processed", "EBL"), config)

        val transaction = (result as ExtractionResult.Matched).transaction
        assertEquals(BigDecimal("12450"), transaction.amount)
    }

    @Test
    fun `given a message with no amount pattern in the body, when extracted, then UnknownSender is returned`() {
        val config = configFor("DBBL")

        val result = extractor.extract(message("Your monthly account statement is ready", "DBBL"), config)

        assertTrue(result is ExtractionResult.UnknownSender)
    }

    @Test
    fun `given a fixed SMS receipt timestamp, when extracted, then date and time come from it`() {
        val config = configFor("DBBL")
        val fixedInstant = Instant.parse("2026-03-15T09:30:00Z")

        val result = extractor.extract(message("Tk 500 withdrawn", "DBBL", timestamp = fixedInstant), config)

        val transaction = (result as ExtractionResult.Matched).transaction
        val expectedZoned = fixedInstant.atZone(ZoneOffset.UTC)
        assertEquals(expectedZoned.toLocalDate(), transaction.date)
        assertEquals(expectedZoned.toLocalTime(), transaction.time)
    }
}
