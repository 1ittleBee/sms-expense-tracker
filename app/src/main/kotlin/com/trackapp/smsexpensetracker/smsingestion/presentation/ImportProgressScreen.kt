package com.trackapp.smsexpensetracker.smsingestion.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackapp.smsexpensetracker.smsingestion.domain.model.ImportProgress

@Composable
fun ImportProgressScreen(
    viewModel: ImportViewModel = hiltViewModel(),
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startImportIfNeeded()
    }

    ImportProgressContent(progress = progress)
}

/** Stateless rendering, extracted for Compose UI testing without a Hilt ViewModel. */
@Composable
fun ImportProgressContent(progress: ImportProgress) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (progress.isComplete) {
                Text(text = "Import complete", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${progress.qualifiedCount} transactions found out of " +
                        "${progress.scannedCount} messages scanned",
                    style = MaterialTheme.typography.bodyLarge,
                )
            } else {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Importing transaction history…", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${progress.scannedCount} scanned · ${progress.qualifiedCount} found",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}
