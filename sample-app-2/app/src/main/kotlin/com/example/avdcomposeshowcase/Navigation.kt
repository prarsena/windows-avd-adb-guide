package com.example.avdcomposeshowcase

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.avdcomposeshowcase.ui.main.OverviewScreen
import com.example.avdcomposeshowcase.ui.main.PatternsScreen
import com.example.avdcomposeshowcase.ui.main.SettingsScreen
import com.example.avdcomposeshowcase.ui.main.ShowcaseViewModel
import com.example.avdcomposeshowcase.ui.theme.AVDComposeShowcaseTheme

@Composable
fun ShowcaseApp(viewModel: ShowcaseViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val backStack = rememberNavBackStack(Overview)
  val selectedDestination = backStack.lastOrNull()

  AVDComposeShowcaseTheme(dynamicColorsEnabled = uiState.dynamicColorsEnabled) {
    NavigationSuiteScaffold(
      navigationSuiteItems = {
        item(
          selected = selectedDestination == Overview,
          onClick = {
            backStack.clear()
            backStack.add(Overview)
          },
          icon = { Icon(Icons.Filled.Home, contentDescription = null) },
          label = { androidx.compose.material3.Text(stringResource(R.string.nav_overview)) },
        )
        item(
          selected = selectedDestination == Patterns,
          onClick = {
            backStack.clear()
            backStack.add(Patterns)
          },
          icon = { Icon(Icons.Filled.Info, contentDescription = null) },
          label = { androidx.compose.material3.Text(stringResource(R.string.nav_patterns)) },
        )
        item(
          selected = selectedDestination == Settings,
          onClick = {
            backStack.clear()
            backStack.add(Settings)
          },
          icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
          label = { androidx.compose.material3.Text(stringResource(R.string.nav_settings)) },
        )
      },
    ) {
      NavDisplay(
        modifier = Modifier,
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        entryProvider = entryProvider {
          entry<Overview> {
            OverviewScreen(
              uiState = uiState,
              onIncrement = viewModel::incrementTap,
              onOpenPatterns = { backStack.add(Patterns) },
              onOpenSettings = { backStack.add(Settings) },
            )
          }
          entry<Patterns> {
            PatternsScreen(onOpenSettings = { backStack.add(Settings) })
          }
          entry<Settings> {
            SettingsScreen(
              uiState = uiState,
              onDynamicColorsChanged = viewModel::setDynamicColorsEnabled,
            )
          }
        },
      )
    }
  }
}
