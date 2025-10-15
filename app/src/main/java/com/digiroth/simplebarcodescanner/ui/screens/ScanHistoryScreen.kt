/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.digiroth.simplebarcodescanner.R
import com.digiroth.simplebarcodescanner.data.Scan
import com.digiroth.simplebarcodescanner.ui.components.ScanHistoryItem
import com.google.mlkit.vision.barcode.common.Barcode

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scan_history)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
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

///**
// * Converts a barcode format integer constant from ML Kit's [Barcode] class into a
// * human-readable string.
// *
// * @param format The integer constant representing the barcode format.
// * @return A string name for the format (e.g., "QR Code", "Code 128"), or "Unknown Format" if the
// *         integer is not recognized.
// */
//fun getBarcodeFormatName(format: Int): String {
//    return when (format) {
//        Barcode.FORMAT_CODE_128 -> "Code 128"
//        Barcode.FORMAT_CODE_39 -> "Code 39"
//        Barcode.FORMAT_CODE_93 -> "Code 93"
//        Barcode.FORMAT_CODABAR -> "Codabar"
//        Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
//        Barcode.FORMAT_EAN_13 -> "EAN-13"
//        Barcode.FORMAT_EAN_8 -> "EAN-8"
//        Barcode.FORMAT_ITF -> "ITF"
//        Barcode.FORMAT_QR_CODE -> "QR Code"
//        Barcode.FORMAT_UPC_A -> "UPC-A"
//        Barcode.FORMAT_UPC_E -> "UPC-E"
//        Barcode.FORMAT_PDF417 -> "PDF417"
//        Barcode.FORMAT_AZTEC -> "Aztec"
//        else -> "Unknown Format"
//    }
//}
