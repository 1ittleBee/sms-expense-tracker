package com.trackapp.smsexpensetracker.transactionparser.domain.model

data class ClassificationRule(
    val type: TransactionType,
    val keywords: List<String>,
)
