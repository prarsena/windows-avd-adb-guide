package com.example.avdcomposeshowcase.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ShowcaseUiState(
  val tapCount: Int = 0,
  val dynamicColorsEnabled: Boolean = true,
)

class ShowcaseViewModel(
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
  private val _uiState =
    MutableStateFlow(
      ShowcaseUiState(
        tapCount = savedStateHandle[TAP_COUNT_KEY] ?: 0,
        dynamicColorsEnabled = savedStateHandle[DYNAMIC_COLORS_KEY] ?: true,
      )
    )

  val uiState = _uiState.asStateFlow()

  fun incrementTap() {
    val updated = _uiState.value.tapCount + 1
    savedStateHandle[TAP_COUNT_KEY] = updated
    _uiState.value = _uiState.value.copy(tapCount = updated)
  }

  fun setDynamicColorsEnabled(enabled: Boolean) {
    savedStateHandle[DYNAMIC_COLORS_KEY] = enabled
    _uiState.value = _uiState.value.copy(dynamicColorsEnabled = enabled)
  }

  private companion object {
    const val TAP_COUNT_KEY = "tap_count"
    const val DYNAMIC_COLORS_KEY = "dynamic_colors_enabled"
  }
}
