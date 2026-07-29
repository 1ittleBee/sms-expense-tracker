package com.trackapp.smsexpensetracker.transactionparser.domain.model

data class UnrecognizedSms(
    val rawBody: String,
    val sender: String,
    val reason: String,
)
