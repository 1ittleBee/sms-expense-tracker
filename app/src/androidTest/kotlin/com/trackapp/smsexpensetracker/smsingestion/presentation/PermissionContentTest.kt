package com.trackapp.smsexpensetracker.smsingestion.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.trackapp.smsexpensetracker.smsingestion.domain.model.DefaultRationaleCopy
import com.trackapp.smsexpensetracker.smsingestion.domain.model.PermissionState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PermissionContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenNotRequestedState_whenRendered_thenShowsRationaleAndContinueButton() {
        composeTestRule.setContent {
            PermissionContent(state = PermissionState.NotRequested, onRequestClick = {}, onOpenSettingsClick = {})
        }

        composeTestRule.onNodeWithText(DefaultRationaleCopy.value.title).assertExists()
        composeTestRule.onNodeWithText("Continue").assertExists()
    }

    @Test
    fun givenDeniedState_whenRendered_thenShowsRetryButton() {
        composeTestRule.setContent {
            PermissionContent(state = PermissionState.Denied, onRequestClick = {}, onOpenSettingsClick = {})
        }

        composeTestRule.onNodeWithText("Retry").assertExists()
    }

    @Test
    fun givenRetryButton_whenClicked_thenOnRequestClickInvoked() {
        var clicked = false
        composeTestRule.setContent {
            PermissionContent(
                state = PermissionState.Denied,
                onRequestClick = { clicked = true },
                onOpenSettingsClick = {},
            )
        }

        composeTestRule.onNodeWithText("Retry").performClick()

        assertTrue(clicked)
    }

    @Test
    fun givenPermanentlyDeniedState_whenRendered_thenShowsOpenSettingsButton() {
        composeTestRule.setContent {
            PermissionContent(state = PermissionState.PermanentlyDenied, onRequestClick = {}, onOpenSettingsClick = {})
        }

        composeTestRule.onNodeWithText("Open Settings").assertExists()
    }

    @Test
    fun givenOpenSettingsButton_whenClicked_thenOnOpenSettingsClickInvoked() {
        var clicked = false
        composeTestRule.setContent {
            PermissionContent(
                state = PermissionState.PermanentlyDenied,
                onRequestClick = {},
                onOpenSettingsClick = { clicked = true },
            )
        }

        composeTestRule.onNodeWithText("Open Settings").performClick()

        assertTrue(clicked)
    }

    @Test
    fun givenGrantedState_whenRendered_thenShowsGrantedMessage() {
        composeTestRule.setContent {
            PermissionContent(state = PermissionState.Granted, onRequestClick = {}, onOpenSettingsClick = {})
        }

        composeTestRule.onNodeWithText("Permission granted").assertExists()
    }
}
