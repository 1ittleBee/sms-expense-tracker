package com.trackapp.smsexpensetracker.transactionparser.domain.service

import com.trackapp.smsexpensetracker.transactionparser.domain.model.ClassificationRule
import com.trackapp.smsexpensetracker.transactionparser.domain.model.TransactionType
import javax.inject.Inject

/** See ADR-003 for why this precedence order is fixed and load-bearing - do not reorder casually. */
class TransactionTypeClassifier @Inject constructor() {

    fun classify(body: String): TransactionType {
        val lower = body.lowercase()
        return RULES.firstOrNull { rule -> rule.keywords.any { lower.contains(it) } }?.type
            ?: TransactionType.Expense
    }

    private companion object {
        val RULES = listOf(
            ClassificationRule(TransactionType.AtmWithdrawal, listOf("withdrawn", "atm", "cash withdrawal", "cash out")),
            ClassificationRule(TransactionType.CardPurchase, listOf("purchase", "payment", "card purchase")),
            ClassificationRule(TransactionType.Refund, listOf("refund")),
            ClassificationRule(TransactionType.Transfer, listOf("transfer")),
            ClassificationRule(TransactionType.Income, listOf("salary credited", "deposit", "credit")),
            ClassificationRule(TransactionType.Expense, listOf("debit")),
        )
    }
}
