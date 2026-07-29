package com.trackapp.smsexpensetracker.transactionparser.data.config

import com.trackapp.smsexpensetracker.transactionparser.domain.model.BankParserConfig

/** See Stage 2 design + ADR-002: shared generic grammar, pending real per-bank sample SMS. */
private val AMOUNT_PATTERN = Regex("""(?i)(Tk\.?|BDT)\s*([\d,]+(?:\.\d{1,2})?)""")
private val ACCOUNT_NUMBER_PATTERN = Regex("""(?i)(?:a/?c|account|card)[^\d]{0,15}(\d{3,4})\b""")
private val MERCHANT_PATTERN = Regex("""(?i)\b(?:at|to)\s+([A-Za-z0-9&.'\- ]{2,40})""")

/**
 * 21 configs = 17 distinct banks + 4 MFS providers.
 *
 * Deviation from the original brief's list of "20 banks" (flagging, same transparency pattern
 * as other implementation-stage catches in this project): the brief listed "DBBL" and
 * "Dutch Bangla" as two separate banks, and "Eastern Bank" and "EBL" as two separate banks -
 * but DBBL *is* Dutch-Bangla Bank Limited, and EBL *is* Eastern Bank Limited. These are the same
 * two institutions listed twice under different names, not four distinct banks. Merged here
 * into one config each rather than propagating the duplication.
 */
object BankParserConfigs {

    val all: List<BankParserConfig> = listOf(
        entry("DBBL", "Dutch-Bangla Bank (DBBL)", "DBBL", "DUTCHBANGLA"),
        entry("BRAC", "BRAC Bank", "BRAC", "BRACBANK"),
        entry("CITYBANK", "City Bank", "CITYBANK"),
        entry("EBL", "Eastern Bank Limited (EBL)", "EBL", "EASTERNBANK"),
        entry("ISLAMIBANK", "Islami Bank Bangladesh", "ISLAMIBANK", "IBBL"),
        entry("SCB", "Standard Chartered Bank", "SCB", "STANCHART"),
        entry("HSBC", "HSBC", "HSBC"),
        entry("PRIMEBANK", "Prime Bank", "PRIMEBANK", "PBL"),
        entry("IFIC", "IFIC Bank", "IFIC"),
        entry("NCC", "NCC Bank", "NCCBANK", "NCC"),
        entry("BANKASIA", "Bank Asia", "BANKASIA"),
        entry("MTB", "Mutual Trust Bank", "MTB", "MTBL"),
        entry("UCB", "United Commercial Bank (UCB)", "UCB", "UCBL"),
        entry("SONALI", "Sonali Bank", "SONALI", "SONALIBANK"),
        entry("JANATA", "Janata Bank", "JANATA", "JANATABANK"),
        entry("AGRANI", "Agrani Bank", "AGRANI", "AGRANIBANK"),
        entry("RUPALI", "Rupali Bank", "RUPALI", "RUPALIBANK"),
        entry("BKASH", "bKash", "BKASH"),
        entry("NAGAD", "Nagad", "NAGAD"),
        entry("ROCKET", "Rocket (DBBL Mobile Banking)", "ROCKET"),
        entry("UPAY", "Upay", "UPAY"),
    )

    private fun entry(bankId: String, displayName: String, vararg senderIds: String) = BankParserConfig(
        bankId = bankId,
        displayName = displayName,
        senderIds = senderIds.toList(),
        amountPattern = AMOUNT_PATTERN,
        accountNumberPattern = ACCOUNT_NUMBER_PATTERN,
        merchantPattern = MERCHANT_PATTERN,
    )
}
