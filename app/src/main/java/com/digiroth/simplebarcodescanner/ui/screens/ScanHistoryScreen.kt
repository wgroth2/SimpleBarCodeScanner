/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.digiroth.simplebarcodescanner.R
import com.digiroth.simplebarcodescanner.data.Scan
import com.digiroth.simplebarcodescanner.ui.components.ScanHistoryItem

/**
 * A screen that displays a list of previously scanned barcodes.
 *
 * It observes the list of scans from the [ScanHistoryViewModel] and displays them
 * in a scrollable list. Each item is clickable, navigating to the [ResultScreen].
 *
 * @param onBack A lambda function to be invoked when the user clicks the back arrow.
 * @param onScanHistoryItemClick A lambda function to be invoked when a scan history item is clicked.
 * @param viewModel The [ScanHistoryViewModel] that provides the list of scans.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(
    onBack: () -> Unit,
    onScanHistoryItemClick: (Scan) -> Unit,
    viewModel: ScanHistoryViewModel
) {
    val scans by viewModel.allScans.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scan_history)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (scans.isNotEmpty()) {
                        // TODO: Add string resource for this
                        IconButton(onClick = { showConfirmDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear History")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (scans.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        // TODO: Add string resource for this
                        text = "Your scan history is empty.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(scans) { scan ->
                        ScanHistoryItem(
                            scan = scan,
                            onItemClick = onScanHistoryItemClick,
                            getBarcodeFormatName = { format -> getBarcodeFormatName(format) }
                        )
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            // TODO: Add string resources for these
            title = { Text("Clear History?") },
            text = { Text("Are you sure you want to permanently delete all scan history? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showConfirmDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}