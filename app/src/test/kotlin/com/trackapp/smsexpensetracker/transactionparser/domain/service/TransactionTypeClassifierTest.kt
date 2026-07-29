package com.trackapp.smsexpensetracker.transactionparser.domain.service

import com.trackapp.smsexpensetracker.transactionparser.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionTypeClassifierTest {

    private val classifier = TransactionTypeClassifier()

    @Test
    fun `given "Cash Withdrawal Payment", when classified, then ATM Withdrawal wins over Card Purchase (ADR-003)`() {
        assertEquals(TransactionType.AtmWithdrawal, classifier.classify("Cash Withdrawal Payment of Tk 1000"))
    }

    @Test
    fun `given ATM Withdrawal keywords, when classified, then result is AtmWithdrawal`() {
        assertEquals(TransactionType.AtmWithdrawal, classifier.classify("Tk 500 withdrawn from your account"))
        assertEquals(TransactionType.AtmWithdrawal, classifier.classify("ATM Withdrawal Tk 1000"))
        assertEquals(TransactionType.AtmWithdrawal, classifier.classify("Cash Out Tk 1000"))
    }

    @Test
    fun `given Card Purchase keywords, when classified, then result is CardPurchase`() {
        assertEquals(TransactionType.CardPurchase, classifier.classify("Purchase BDT 900 at STARBUCKS"))
        assertEquals(TransactionType.CardPurchase, classifier.classify("Payment Tk 450 processed"))
    }

    @Test
    fun `given Refund keyword, when classified, then result is Refund`() {
        assertEquals(TransactionType.Refund, classifier.classify("Refund Tk 300 credited"))
    }

    @Test
    fun `given Transfer keyword, when classified, then result is Transfer`() {
        assertEquals(TransactionType.Transfer, classifier.classify("Transfer of Tk 2000 completed"))
    }

    @Test
    fun `given Income keywords, when classified, then result is Income`() {
        assertEquals(TransactionType.Income, classifier.classify("Salary Credited Tk 35000"))
        assertEquals(TransactionType.Income, classifier.classify("BDT 2500 deposited"))
    }

    @Test
    fun `given generic Debit keyword, when classified, then result is Expense`() {
        assertEquals(TransactionType.Expense, classifier.classify("Debit BDT 1200 from your account"))
    }

    @Test
    fun `given no recognizable keyword at all, when classified, then default is Expense`() {
        assertEquals(TransactionType.Expense, classifier.classify("Your account balance is Tk 5000"))
    }
}
