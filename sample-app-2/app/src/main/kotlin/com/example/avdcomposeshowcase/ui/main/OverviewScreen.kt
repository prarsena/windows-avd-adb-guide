package com.example.avdcomposeshowcase.ui.main

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.avdcomposeshowcase.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
  uiState: ShowcaseUiState,
  onIncrement: () -> Unit,
  onOpenPatterns: () -> Unit,
  onOpenSettings: () -> Unit,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      LargeTopAppBar(
        title = { Text(stringResource(R.string.overview_title)) },
        actions = {
          IconButton(onClick = onOpenSettings) {
            Icon(
              imageVector = Icons.Filled.Settings,
              contentDescription = stringResource(R.string.nav_settings),
            )
          }
        },
        scrollBehavior = scrollBehavior,
      )
    },
  ) { innerPadding ->
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      val wideLayout = maxWidth >= 840.dp
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
          horizontal = if (wideLayout) 32.dp else 20.dp,
          vertical = 20.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        item { HeroCard() }
        item {
          if (wideLayout) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
              DeviceCard(modifier = Modifier.weight(1f))
              CounterCard(
                tapCount = uiState.tapCount,
                onIncrement = onIncrement,
                modifier = Modifier.weight(1f),
              )
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
              DeviceCard()
              CounterCard(tapCount = uiState.tapCount, onIncrement = onIncrement)
            }
          }
        }
        item { PatternsCard(onOpenPatterns) }
      }
    }
  }
}

@Composable
private fun HeroCard() {
  Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    shape = MaterialTheme.shapes.extraLarge,
  ) {
    Column(
      modifier = Modifier.padding(24.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(
        text = stringResource(R.string.hero_eyebrow),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
      )
      Text(
        text = stringResource(R.string.hero_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
      )
      Text(
        text = stringResource(R.string.hero_body),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
      )
    }
  }
}

@Composable
private fun DeviceCard(modifier: Modifier = Modifier) {
  ElevatedCard(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.extraLarge,
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = stringResource(R.string.device_card_title),
        style = MaterialTheme.typography.titleMedium,
      )
      Text(
        text = stringResource(R.string.device_model, Build.MODEL),
        style = MaterialTheme.typography.headlineSmall,
      )
      Text(
        text = stringResource(
          R.string.device_api,
          Build.VERSION.SDK_INT,
          Build.VERSION.RELEASE,
        ),
        style = MaterialTheme.typography.bodyMedium,
      )
      Text(
        text = stringResource(R.string.target_api),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

@Composable
private fun CounterCard(
  tapCount: Int,
  onIncrement: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ElevatedCard(
    modifier = modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.extraLarge,
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = stringResource(R.string.counter_card_title),
        style = MaterialTheme.typography.titleMedium,
      )
      Text(
        text = stringResource(R.string.counter_value, tapCount),
        style = MaterialTheme.typography.headlineSmall,
      )
      Text(
        text = stringResource(R.string.counter_body),
        style = MaterialTheme.typography.bodyMedium,
      )
      Spacer(modifier = Modifier.height(4.dp))
      FilledTonalButton(
        onClick = onIncrement,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
      ) {
        Text(stringResource(R.string.counter_button))
      }
    }
  }
}

@Composable
private fun PatternsCard(onOpenPatterns: () -> Unit) {
  Card(
    onClick = onOpenPatterns,
    shape = MaterialTheme.shapes.extraLarge,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
          text = stringResource(R.string.patterns_card_title),
          style = MaterialTheme.typography.titleMedium,
        )
        Text(
          text = stringResource(R.string.patterns_card_body),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
      Text(
        text = stringResource(R.string.open_patterns),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}
