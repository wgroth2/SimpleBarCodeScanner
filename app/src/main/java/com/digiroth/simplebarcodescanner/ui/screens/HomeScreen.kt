/*
 * Copyright 2025 Bill Roth
 */
package com.digiroth.simplebarcodescanner.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiroth.simplebarcodescanner.ui.components.AboutDialog
import com.digiroth.simplebarcodescanner.ui.components.TopAppBarMenu
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanSuccess: (String, Int, Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: SettingsViewModel
) {
    val context: Context = LocalContext.current
    var showAboutDialog by remember { mutableStateOf(false) }

    val isAutoZoomEnabled by viewModel.isAutoZoomEnabled.collectAsState()

    val scanner: GmsBarcodeScanner = remember(isAutoZoomEnabled) {
        Log.d("ScannerInit", "Scanner is being re-initialized with auto-zoom: $isAutoZoomEnabled")

        val optionsBuilder = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_CODABAR,
                Barcode.FORMAT_DATA_MATRIX,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_ITF,
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_PDF417,
                Barcode.FORMAT_AZTEC,
                Barcode.TYPE_DRIVER_LICENSE
            )
        if (isAutoZoomEnabled) {
            optionsBuilder.enableAutoZoom()
        }
        GmsBarcodeScanning.getClient(context, optionsBuilder.build())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDF Barcode Reader") },
                actions = {
                    TopAppBarMenu(
                        onSettingsClick = onNavigateToSettings,
                        onAboutClick = { showAboutDialog = true }
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    scanner.startScan()
                        .addOnSuccessListener { barcode ->
                            val rawValue = barcode.rawValue ?: "No data"
                            val valueType = barcode.valueType
                            val format = barcode.format

                            Log.i("BarcodeSuccess", "Barcode raw value: $rawValue, Type: $valueType, Format: $format")
                            Toast.makeText(context, "Barcode Scanned: $rawValue", Toast.LENGTH_LONG).show()

                            onScanSuccess(rawValue, valueType, format)
                        }
                        .addOnCanceledListener {
                            Log.i("BarcodeCanceled", "Scan canceled by user.")
                            Toast.makeText(context, "Scan canceled", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("BarcodeFailure", "Scan failed", e)
                            Toast.makeText(context, "Scan failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            ) {
                Text(
                    text = "Scan",
                    fontSize = 20.sp
                )
            }
        }
    }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }
}
