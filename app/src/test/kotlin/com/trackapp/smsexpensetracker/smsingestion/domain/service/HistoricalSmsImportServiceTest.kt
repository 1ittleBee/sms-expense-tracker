package com.trackapp.smsexpensetracker.smsingestion.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.ImportProgress
import com.trackapp.smsexpensetracker.smsingestion.domain.model.ImportWindow
import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.smsingestion.domain.model.SmsSource
import com.trackapp.smsexpensetracker.smsingestion.domain.repository.SmsProviderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class HistoricalSmsImportServiceTest {

    private class FakeSmsProviderRepository(private val messages: List<RawSmsMessage>) : SmsProviderRepository {
        override fun queryInbox(since: Instant): Flow<RawSmsMessage> = messages.asFlow()
    }

    private fun message(body: String, sender: String = "DBBL") = RawSmsMessage(
        sender = sender,
        body = body,
        timestamp = Instant.now(),
        source = SmsSource.Import,
    )

    @Test
    fun `given mixed messages, when imported, then only transaction-relevant ones count toward qualified`() = runTest {
        val messages = listOf(
            message("Your OTP is 1234, do not share this code"),
            message("50% discount offer this week, unsubscribe here"),
            message("Tk 500 withdrawn from your account"),
            message("Hey, are we still meeting today?", sender = "+8801700000000"),
        )
        val service = HistoricalSmsImportService(
            smsProviderRepository = FakeSmsProviderRepository(messages),
            relevanceFilter = TransactionRelevanceFilter(),
            smsIntakeBus = SmsIntakeBus(),
        )

        val progressUpdates = mutableListOf<ImportProgress>()
        service.importSince(ImportWindow(Instant.EPOCH)) { progressUpdates.add(it) }

        val finalProgress = progressUpdates.last()
        assertEquals(4, finalProgress.scannedCount)
        assertEquals(1, finalProgress.qualifiedCount)
        assertTrue(finalProgress.isComplete)
    }

    @Test
    fun `given an empty inbox, when imported, then final progress reports zero counts and isComplete true`() = runTest {
        val service = HistoricalSmsImportService(
            smsProviderRepository = FakeSmsProviderRepository(emptyList()),
            relevanceFilter = TransactionRelevanceFilter(),
            smsIntakeBus = SmsIntakeBus(),
        )

        val progressUpdates = mutableListOf<ImportProgress>()
        service.importSince(ImportWindow(Instant.EPOCH)) { progressUpdates.add(it) }

        val finalProgress = progressUpdates.last()
        assertEquals(0, finalProgress.scannedCount)
        assertEquals(0, finalProgress.qualifiedCount)
        assertTrue(finalProgress.isComplete)
    }
}
