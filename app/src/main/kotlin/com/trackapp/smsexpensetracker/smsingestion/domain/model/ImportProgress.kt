package com.trackapp.smsexpensetracker.smsingestion.domain.model

data class ImportProgress(
    val scannedCount: Int = 0,
    val qualifiedCount: Int = 0,
    val isComplete: Boolean = false,
)
