package com.example.avdcomposeshowcase.ui.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.avdcomposeshowcase.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  uiState: ShowcaseUiState,
  onDynamicColorsChanged: (Boolean) -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(title = { Text(stringResource(R.string.settings_title)) })
    },
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(vertical = 12.dp),
    ) {
      item {
        Text(
          text = stringResource(R.string.settings_summary),
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
          style = MaterialTheme.typography.titleMedium,
        )
      }
      item {
        val dynamicColorDescription = stringResource(R.string.dynamic_color_accessibility)
        ListItem(
          headlineContent = { Text(stringResource(R.string.dynamic_color_title)) },
          supportingContent = { Text(stringResource(R.string.dynamic_color_body)) },
          trailingContent = {
            Switch(
              checked = uiState.dynamicColorsEnabled,
              onCheckedChange = onDynamicColorsChanged,
              modifier = Modifier.semantics { contentDescription = dynamicColorDescription },
            )
          },
        )
      }
      item {
        Text(
          text = stringResource(R.string.system_theme_note),
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
      item {
        ListItem(
          headlineContent = { Text(stringResource(R.string.api_target_title)) },
          supportingContent = { Text(stringResource(R.string.api_target_body)) },
        )
      }
    }
  }
}
