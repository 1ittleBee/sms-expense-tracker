package com.trackapp.smsexpensetracker.smsingestion.domain.model

sealed class FilterResult {
    data object Include : FilterResult()
    data object ExcludeOtp : FilterResult()
    data object ExcludePromotional : FilterResult()

    /**
     * Added during implementation: the Stage 1 domain model only named the two exclusion
     * reasons explicitly called out in the stories (OTP, promotional). Anything that is
     * neither OTP/promotional NOR recognizably transaction-related (no known sender, no
     * transaction keyword) still needs a result - falling through to `Include` would let
     * arbitrary personal SMS reach the parser.
     */
    data object ExcludeNotTransactional : FilterResult()
}
