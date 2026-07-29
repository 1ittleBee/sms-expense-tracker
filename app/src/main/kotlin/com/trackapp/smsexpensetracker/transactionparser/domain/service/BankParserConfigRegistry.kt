package com.trackapp.smsexpensetracker.transactionparser.domain.service

import com.trackapp.smsexpensetracker.transactionparser.data.config.BankParserConfigs
import com.trackapp.smsexpensetracker.transactionparser.domain.model.BankParserConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BankParserConfigRegistry @Inject constructor() {

    private val configs: List<BankParserConfig> = BankParserConfigs.all

    /** Direct sender-ID lookup only - body-keyword fallback is bolt 004's story 003. */
    fun findBySenderId(sender: String): BankParserConfig? {
        val normalized = sender.trim().uppercase()
        return configs.firstOrNull { config -> config.senderIds.any { normalized.contains(it) } }
    }

    fun allConfigs(): List<BankParserConfig> = configs
}
