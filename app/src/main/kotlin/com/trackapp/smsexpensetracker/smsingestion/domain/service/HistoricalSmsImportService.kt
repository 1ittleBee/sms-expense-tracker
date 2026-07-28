package com.trackapp.smsexpensetracker.smsingestion.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.FilterResult
import com.trackapp.smsexpensetracker.smsingestion.domain.model.ImportProgress
import com.trackapp.smsexpensetracker.smsingestion.domain.model.ImportWindow
import com.trackapp.smsexpensetracker.smsingestion.domain.repository.SmsProviderRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoricalSmsImportService @Inject constructor(
    private val smsProviderRepository: SmsProviderRepository,
    private val relevanceFilter: TransactionRelevanceFilter,
    private val smsIntakeBus: SmsIntakeBus,
) {
    /**
     * Scans the inbox from [window.since] onward, filters each message, publishes qualifying
     * ones to [SmsIntakeBus], and reports running progress via [onProgress].
     */
    suspend fun importSince(window: ImportWindow, onProgress: suspend (ImportProgress) -> Unit) {
        var scanned = 0
        var qualified = 0

        smsProviderRepository.queryInbox(window.since).collect { message ->
            scanned++
            if (relevanceFilter.classify(message) is FilterResult.Include) {
                qualified++
                smsIntakeBus.publish(message)
            }
            onProgress(ImportProgress(scannedCount = scanned, qualifiedCount = qualified, isComplete = false))
        }

        onProgress(ImportProgress(scannedCount = scanned, qualifiedCount = qualified, isComplete = true))
    }
}
