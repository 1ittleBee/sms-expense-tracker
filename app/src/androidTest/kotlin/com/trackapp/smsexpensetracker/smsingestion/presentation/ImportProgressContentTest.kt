package com.trackapp.smsexpensetracker.smsingestion.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.trackapp.smsexpensetracker.smsingestion.domain.model.ImportProgress
import org.junit.Rule
import org.junit.Test

class ImportProgressContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenInProgressState_whenRendered_thenShowsScannedAndFoundCounts() {
        composeTestRule.setContent {
            ImportProgressContent(progress = ImportProgress(scannedCount = 42, qualifiedCount = 5, isComplete = false))
        }

        composeTestRule.onNodeWithText("42 scanned · 5 found").assertExists()
    }

    @Test
    fun givenCompleteState_whenRendered_thenShowsCompletionSummary() {
        composeTestRule.setContent {
            ImportProgressContent(progress = ImportProgress(scannedCount = 100, qualifiedCount = 20, isComplete = true))
        }

        composeTestRule.onNodeWithText("Import complete").assertExists()
        composeTestRule.onNodeWithText("20 transactions found out of 100 messages scanned").assertExists()
    }
}
