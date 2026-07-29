package com.trackapp.smsexpensetracker.transactionparser.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.transactionparser.domain.model.ExtractionResult
import com.trackapp.smsexpensetracker.transactionparser.domain.model.ParsingOutcome
import com.trackapp.smsexpensetracker.transactionparser.domain.model.UnrecognizedSms
import javax.inject.Inject

/**
 * The transaction-parser unit's real entry point. Not yet wired into SmsIntakeBus - unit
 * 003-transaction-persistence doesn't exist yet, so there's nowhere to route ParsingOutcome to
 * until that bolt lands.
 */
class TransactionParser @Inject constructor(
    private val senderResolver: SenderResolver,
    private val fieldExtractor: TransactionFieldExtractor,
    private val classifier: TransactionTypeClassifier,
) {
    fun parse(message: RawSmsMessage): ParsingOutcome {
        val config = senderResolver.resolve(message)
            ?: return ParsingOutcome.SenderUnknown(message)

        return when (val extraction = fieldExtractor.extract(message, config)) {
            is ExtractionResult.Matched -> {
                val classified = extraction.transaction.copy(
                    transactionType = classifier.classify(message.body),
                )
                ParsingOutcome.Classified(classified)
            }

            // Per Stage 2's naming nuance: UnknownSender here always means "sender known
            // (config was resolved above), body didn't match a field pattern" - never
            // "sender unknown", since that case already returned above.
            is ExtractionResult.UnknownSender -> ParsingOutcome.FlaggedForReview(
                UnrecognizedSms(
                    rawBody = message.body,
                    sender = message.sender,
                    reason = "No field pattern matched for bank ${config.bankId}",
                ),
            )
        }
    }
}
