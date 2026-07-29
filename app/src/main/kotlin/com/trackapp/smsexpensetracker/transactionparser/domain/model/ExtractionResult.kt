package com.trackapp.smsexpensetracker.transactionparser.domain.model

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage

sealed class ExtractionResult {
    data class Matched(val transaction: ParsedTransaction) : ExtractionResult()

    /**
     * Covers both "sender matches no known bank/MFS" and "sender matched but no amount pattern
     * found in the body" - this bolt does not distinguish them further. Bolt 004 (story 003's
     * body-keyword fallback and story 005's unrecognized-body flagging) refines this.
     */
    data class UnknownSender(val message: RawSmsMessage) : ExtractionResult()
}
