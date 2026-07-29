package com.trackapp.smsexpensetracker.transactionparser.domain.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BankParserConfigRegistryTest {

    private val registry = BankParserConfigRegistry()

    @Test
    fun `given a known sender ID, when looked up, then the matching bank config is returned`() {
        val config = registry.findBySenderId("DBBL")

        assertEquals("DBBL", config?.bankId)
    }

    @Test
    fun `given a known sender ID in lowercase, when looked up, then it still matches case-insensitively`() {
        val config = registry.findBySenderId("bkash")

        assertEquals("BKASH", config?.bankId)
    }

    @Test
    fun `given an unrecognized sender ID, when looked up, then null is returned`() {
        val config = registry.findBySenderId("+8801700000000")

        assertNull(config)
    }

    @Test
    fun `given every bundled config, when each of its own sender IDs is looked up, then it resolves back to itself`() {
        registry.allConfigs().forEach { config ->
            config.senderIds.forEach { senderId ->
                val resolved = registry.findBySenderId(senderId)
                assertEquals("sender $senderId should resolve to ${config.bankId}", config.bankId, resolved?.bankId)
            }
        }
    }
}
