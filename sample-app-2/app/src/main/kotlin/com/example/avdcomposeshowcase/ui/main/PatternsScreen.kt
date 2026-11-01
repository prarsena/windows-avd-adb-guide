package com.example.avdcomposeshowcase.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.avdcomposeshowcase.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternsScreen(onOpenSettings: () -> Unit) {
  Scaffold(
    topBar = {
      LargeTopAppBar(
        title = { Text(stringResource(R.string.patterns_title)) },
        actions = {
          IconButton(onClick = onOpenSettings) {
            Icon(
              imageVector = Icons.Filled.Settings,
              contentDescription = stringResource(R.string.nav_settings),
            )
          }
        },
      )
    },
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        Text(
          text = stringResource(R.string.patterns_intro),
          style = MaterialTheme.typography.bodyLarge,
        )
      }
      item {
        PatternCard(
          title = stringResource(R.string.pattern_compose_title),
          description = stringResource(R.string.pattern_compose_body),
        )
      }
      item {
        PatternCard(
          title = stringResource(R.string.pattern_udf_title),
          description = stringResource(R.string.pattern_udf_body),
        )
      }
      item {
        PatternCard(
          title = stringResource(R.string.pattern_lifecycle_title),
          description = stringResource(R.string.pattern_lifecycle_body),
        )
      }
      item {
        PatternCard(
          title = stringResource(R.string.pattern_navigation_title),
          description = stringResource(R.string.pattern_navigation_body),
        )
      }
      item {
        PatternCard(
          title = stringResource(R.string.pattern_material_title),
          description = stringResource(R.string.pattern_material_body),
        )
      }
      item {
        PatternCard(
          title = stringResource(R.string.pattern_edge_title),
          description = stringResource(R.string.pattern_edge_body),
        )
      }
    }
  }
}

@Composable
private fun PatternCard(
  title: String,
  description: String,
) {
  Card(shape = MaterialTheme.shapes.large) {
    Column(
      modifier = Modifier.padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(text = title, style = MaterialTheme.typography.titleMedium)
      Text(text = description, style = MaterialTheme.typography.bodyMedium)
    }
  }
}
