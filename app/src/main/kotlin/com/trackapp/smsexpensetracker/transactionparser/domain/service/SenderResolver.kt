package com.trackapp.smsexpensetracker.transactionparser.domain.service

import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.transactionparser.domain.model.BankParserConfig
import javax.inject.Inject

class SenderResolver @Inject constructor(
    private val registry: BankParserConfigRegistry,
) {
    /**
     * Direct sender-ID lookup always wins when present. Fallback only scans the body for
     * bankId/senderIds tokens (never displayName - see Stage 2 design for why) when the sender
     * ID alone doesn't resolve.
     */
    fun resolve(message: RawSmsMessage): BankParserConfig? {
        registry.findBySenderId(message.sender)?.let { return it }

        val body = message.body.uppercase()
        return registry.allConfigs().firstOrNull { config ->
            body.contains(config.bankId) || config.senderIds.any { body.contains(it) }
        }
    }
}
