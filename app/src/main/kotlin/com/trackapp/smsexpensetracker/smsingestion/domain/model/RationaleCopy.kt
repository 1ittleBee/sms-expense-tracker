package com.trackapp.smsexpensetracker.smsingestion.domain.model

data class RationaleCopy(
    val title: String,
    val body: String,
)

object DefaultRationaleCopy {
    val value = RationaleCopy(
        title = "Read transaction SMS",
        body = "This app reads your bank and mobile money SMS to automatically track transactions. " +
            "It never sends SMS, and your messages never leave this device.",
    )
}
