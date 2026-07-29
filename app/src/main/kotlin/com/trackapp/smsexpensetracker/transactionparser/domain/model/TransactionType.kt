package com.trackapp.smsexpensetracker.transactionparser.domain.model

/**
 * [Unclassified] is the placeholder value this bolt (003-transaction-parser) produces - real
 * classification into the other cases is bolt 004-transaction-parser's job (story 004).
 */
enum class TransactionType {
    Unclassified,
    Expense,
    Income,
    Transfer,
    AtmWithdrawal,
    CardPurchase,
    Refund,
}
