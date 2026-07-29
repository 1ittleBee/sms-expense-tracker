package com.trackapp.smsexpensetracker.transactionparser.domain.model

/**
 * Per ADR-002: all current entries share the same generic pattern objects (see
 * data/config/BankParserConfigs.kt) - only bankId/displayName/senderIds actually differ today.
 * The per-entry pattern fields exist so a specific bank's patterns can be overridden later
 * without any change to the engine, once real sample SMS justify doing so.
 */
data class BankParserConfig(
    val bankId: String,
    val displayName: String,
    val senderIds: List<String>,
    val amountPattern: Regex,
    val accountNumberPattern: Regex,
    val merchantPattern: Regex,
)
