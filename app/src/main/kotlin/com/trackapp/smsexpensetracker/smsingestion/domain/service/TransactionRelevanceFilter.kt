package com.trackapp.smsexpensetracker.smsingestion.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.FilterResult
import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import javax.inject.Inject

/**
 * Coarse relevance filter shared by both the import and live-detection pipelines. This is
 * deliberately lightweight (keyword/sender matching, not per-bank regex) - precise field
 * extraction and bank attribution is unit 002-transaction-parser's job. This filter only
 * decides whether a message is worth forwarding there at all.
 */
class TransactionRelevanceFilter @Inject constructor() {

    fun classify(message: RawSmsMessage): FilterResult {
        val body = message.body.lowercase()
        val sender = message.sender.uppercase()

        if (OTP_KEYWORDS.any { body.contains(it) }) return FilterResult.ExcludeOtp
        if (PROMOTIONAL_KEYWORDS.any { body.contains(it) }) return FilterResult.ExcludePromotional

        val looksTransactional = TRANSACTION_KEYWORDS.any { body.contains(it) } ||
            KNOWN_SENDERS.any { sender.contains(it) }

        return if (looksTransactional) FilterResult.Include else FilterResult.ExcludeNotTransactional
    }

    private companion object {
        val OTP_KEYWORDS = listOf(
            "otp",
            "one time password",
            "one-time password",
            "verification code",
            "is your code",
            "do not share this code",
        )

        val PROMOTIONAL_KEYWORDS = listOf(
            "offer",
            "cashback bonus",
            "% discount",
            "unsubscribe",
            "limited time offer",
            "win a",
            "congratulations you have won",
        )

        val TRANSACTION_KEYWORDS = listOf(
            "debit",
            "credit",
            "withdrawn",
            "withdrawal",
            "purchase",
            "deposit",
            "transfer",
            "salary credited",
            "cash out",
            "payment",
            "refund",
            "atm",
            "tk ",
            "bdt ",
        )

        val KNOWN_SENDERS = listOf(
            "DBBL", "BRAC", "CITYBANK", "EBL", "UCB", "SONALI", "JANATA", "AGRANI",
            "RUPALI", "ISLAMIBANK", "DUTCHBANGLA", "SCB", "HSBC", "PRIMEBANK", "IFIC",
            "NCC", "BANKASIA", "MTB", "BKASH", "NAGAD", "ROCKET", "UPAY",
        )
    }
}
