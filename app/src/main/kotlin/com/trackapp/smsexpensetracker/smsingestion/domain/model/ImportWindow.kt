package com.trackapp.smsexpensetracker.smsingestion.domain.model

import java.time.Instant
import java.time.ZoneOffset

data class ImportWindow(val since: Instant) {
    companion object {
        private const val IMPORT_MONTHS = 6L

        /**
         * Computed via [java.time.ZonedDateTime] (calendar-aware), not [Instant.minus] with
         * [java.time.temporal.ChronoUnit.MONTHS] - Instant has no calendar context and throws
         * UnsupportedTemporalTypeException for estimated units like months/years.
         */
        fun last6Months(now: Instant = Instant.now()): ImportWindow =
            ImportWindow(since = now.atZone(ZoneOffset.UTC).minusMonths(IMPORT_MONTHS).toInstant())
    }
}
