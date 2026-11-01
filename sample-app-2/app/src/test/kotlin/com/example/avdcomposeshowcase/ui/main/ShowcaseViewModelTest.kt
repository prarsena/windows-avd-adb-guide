package com.example.avdcomposeshowcase.ui.main

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Test

class ShowcaseViewModelTest {
  @Test
  fun incrementTap_updatesAndPersistsCounter() {
    val savedStateHandle = SavedStateHandle()
    val viewModel = ShowcaseViewModel(savedStateHandle)

    viewModel.incrementTap()

    assertEquals(1, viewModel.uiState.value.tapCount)
    assertEquals(1, savedStateHandle.get<Int>("tap_count"))
  }

  @Test
  fun initialState_restoresSavedValues() {
    val savedStateHandle =
      SavedStateHandle(
        mapOf(
          "tap_count" to 4,
          "dynamic_colors_enabled" to false,
        )
      )

    val viewModel = ShowcaseViewModel(savedStateHandle)

    assertEquals(4, viewModel.uiState.value.tapCount)
    assertEquals(false, viewModel.uiState.value.dynamicColorsEnabled)
  }
}
