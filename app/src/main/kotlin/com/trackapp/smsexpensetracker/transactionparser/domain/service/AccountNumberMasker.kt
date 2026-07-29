package com.trackapp.smsexpensetracker.transactionparser.domain.service

import javax.inject.Inject

/**
 * Formats the trailing digits already captured by a bounded account-number regex (which itself
 * only ever captures 3-4 digits - see BankParserConfigs) into a display form. A full account
 * number is never extracted in the first place, so there is nothing to redact here beyond
 * formatting.
 */
class AccountNumberMasker @Inject constructor() {
    fun format(trailingDigits: String): String = "****$trailingDigits"
}
