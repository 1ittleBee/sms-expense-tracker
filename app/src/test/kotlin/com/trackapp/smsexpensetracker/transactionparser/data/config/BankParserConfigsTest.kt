package com.trackapp.smsexpensetracker.transactionparser.data.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BankParserConfigsTest {

    @Test
    fun `given the bundled config, when counted, then there are 21 entries (17 banks + 4 MFS)`() {
        assertEquals(21, BankParserConfigs.all.size)
    }

    @Test
    fun `given the bundled config, when bank IDs inspected, then all are unique`() {
        val bankIds = BankParserConfigs.all.map { it.bankId }

        assertEquals(bankIds.size, bankIds.toSet().size)
    }

    @Test
    fun `given the bundled config, when sender IDs inspected, then no sender ID is claimed by two banks`() {
        val allSenderIds = BankParserConfigs.all.flatMap { it.senderIds }

        assertEquals(allSenderIds.size, allSenderIds.toSet().size)
    }

    @Test
    fun `given the previously-duplicated institutions, when inspected, then DBBL and EBL are each represented exactly once`() {
        val dbblEntries = BankParserConfigs.all.filter { it.bankId == "DBBL" }
        val eblEntries = BankParserConfigs.all.filter { it.bankId == "EBL" }

        assertEquals(1, dbblEntries.size)
        assertEquals(1, eblEntries.size)
        assertTrue(dbblEntries.first().senderIds.containsAll(listOf("DBBL", "DUTCHBANGLA")))
        assertTrue(eblEntries.first().senderIds.containsAll(listOf("EBL", "EASTERNBANK")))
    }
}
