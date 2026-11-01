package com.example.avdcomposeshowcase.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.avdcomposeshowcase.ui.theme.AVDComposeShowcaseTheme
import org.junit.Rule
import org.junit.Test

class ShowcaseAppTest {
  @get:Rule
  val composeRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun overview_showsDeviceAndSavedCounter() {
    composeRule.setContent {
      AVDComposeShowcaseTheme(dynamicColorsEnabled = false) {
        OverviewScreen(
          uiState = ShowcaseUiState(),
          onIncrement = {},
          onOpenPatterns = {},
          onOpenSettings = {},
        )
      }
    }

    composeRule.onNodeWithText("Running device").assertIsDisplayed()
    composeRule.onNodeWithText("Tap count: 0").assertIsDisplayed()
  }

  @Test
  fun incrementButton_updatesDisplayedCount() {
    var uiState by mutableStateOf(ShowcaseUiState())
    composeRule.setContent {
      AVDComposeShowcaseTheme(dynamicColorsEnabled = false) {
        OverviewScreen(
          uiState = uiState,
          onIncrement = { uiState = uiState.copy(tapCount = uiState.tapCount + 1) },
          onOpenPatterns = {},
          onOpenSettings = {},
        )
      }
    }

    composeRule.onNodeWithText("Increment counter").performClick()
    composeRule.onNodeWithText("Tap count: 1").assertIsDisplayed()
  }
}
