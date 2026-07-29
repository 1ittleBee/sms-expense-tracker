package com.trackapp.smsexpensetracker.transactionparser.domain.service

import org.junit.Assert.assertEquals
import org.junit.Test

class AccountNumberMaskerTest {

    private val masker = AccountNumberMasker()

    @Test
    fun `given 4 trailing digits, when formatted, then prefixed with asterisks`() {
        assertEquals("****1234", masker.format("1234"))
    }

    @Test
    fun `given 3 trailing digits, when formatted, then still masked correctly`() {
        assertEquals("****123", masker.format("123"))
    }
}
