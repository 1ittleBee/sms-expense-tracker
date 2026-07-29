package com.trackapp.smsexpensetracker.transactionparser.domain.model

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage

sealed class ParsingOutcome {
    /** Fully resolved - sender known, fields extracted, type classified. Ready for persistence. */
    data class Classified(val transaction: ParsedTransaction) : ParsingOutcome()

    /** Sender known (directly or via fallback) but no field pattern matched the body. */
    data class FlaggedForReview(val flagged: UnrecognizedSms) : ParsingOutcome()

    /** Sender not resolvable even with body-keyword fallback. */
    data class SenderUnknown(val message: RawSmsMessage) : ParsingOutcome()
}
