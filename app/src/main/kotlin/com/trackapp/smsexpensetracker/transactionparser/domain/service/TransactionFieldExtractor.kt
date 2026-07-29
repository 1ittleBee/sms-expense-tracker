package com.trackapp.smsexpensetracker.transactionparser.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.transactionparser.domain.model.BankParserConfig
import com.trackapp.smsexpensetracker.transactionparser.domain.model.ExtractionResult
import com.trackapp.smsexpensetracker.transactionparser.domain.model.ParsedTransaction
import com.trackapp.smsexpensetracker.transactionparser.domain.model.TransactionType
import java.math.BigDecimal
import java.time.ZoneOffset
import javax.inject.Inject

/**
 * Deviation from Stage 2 design (flagging, same as prior bolts): date/time extraction always
 * uses the SMS receipt timestamp rather than attempting a generic in-body date/time regex. An
 * invented in-body date pattern would carry the same unverified-accuracy risk as ADR-002's
 * shared amount grammar, but for an even less certain format (the brief's examples never show
 * an in-body date at all) - the receipt timestamp is real, reliable data instead of a guess.
 */
class TransactionFieldExtractor @Inject constructor(
    private val accountNumberMasker: AccountNumberMasker,
) {

    fun extract(message: RawSmsMessage, config: BankParserConfig): ExtractionResult {
        val amountMatch = config.amountPattern.find(message.body)
            ?: return ExtractionResult.UnknownSender(message)

        val amount = runCatching {
            BigDecimal(amountMatch.groupValues[2].replace(",", ""))
        }.getOrNull() ?: return ExtractionResult.UnknownSender(message)

        val maskedAccountNumber = config.accountNumberPattern.find(message.body)
            ?.groupValues
            ?.get(1)
            ?.let(accountNumberMasker::format)

        val merchant = config.merchantPattern.find(message.body)
            ?.groupValues
            ?.get(1)
            ?.trim()

        val receivedAt = message.timestamp.atZone(ZoneOffset.UTC)

        val transaction = ParsedTransaction(
            amount = amount,
            currency = CURRENCY,
            date = receivedAt.toLocalDate(),
            time = receivedAt.toLocalTime(),
            bankId = config.bankId,
            maskedAccountNumber = maskedAccountNumber,
            merchant = merchant,
            transactionType = TransactionType.Unclassified,
        )

        return ExtractionResult.Matched(transaction)
    }

    private companion object {
        // Only BDT is in scope for this app - no multi-currency requirement was ever stated.
        const val CURRENCY = "BDT"
    }
}
