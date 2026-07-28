package com.trackapp.smsexpensetracker.smsingestion.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset

class ImportWindowTest {

    @Test
    fun `given a fixed now, when computing last 6 months, then since is exactly 6 calendar months earlier`() {
        val now = Instant.parse("2026-07-28T12:00:00Z")
        val expected = now.atZone(ZoneOffset.UTC).minusMonths(6).toInstant()

        val window = ImportWindow.last6Months(now)

        assertEquals(expected, window.since)
    }

    @Test
    fun `given now at a month-end edge case, when computing last 6 months, then no exception is thrown`() {
        // Regression check for the pitfall this class was written to avoid: Instant.minus with
        // ChronoUnit.MONTHS throws UnsupportedTemporalTypeException, since Instant has no
        // calendar context. ImportWindow routes through ZonedDateTime instead.
        val now = Instant.parse("2026-01-31T00:00:00Z")

        val window = ImportWindow.last6Months(now)

        assertEquals(now.atZone(ZoneOffset.UTC).minusMonths(6).toInstant(), window.since)
    }
}
