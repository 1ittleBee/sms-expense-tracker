package com.trackapp.smsexpensetracker.transactionparser.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

data class ParsedTransaction(
    val amount: BigDecimal,
    val currency: String,
    val date: LocalDate,
    val time: LocalTime,
    val bankId: String,
    val maskedAccountNumber: String?,
    val merchant: String?,
    val transactionType: TransactionType,
)
