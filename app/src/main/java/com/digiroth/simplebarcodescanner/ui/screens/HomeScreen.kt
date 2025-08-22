package com.digiroth.simplebarcodescanner.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.digiroth.simplebarcodescanner.BuildConfig
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanSuccess: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context: Context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Read the preference. This will be re-evaluated on every recomposition.
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val isAutoZoomEnabled = sharedPreferences.getBoolean("auto_zoom", true)

    // Scanner initialization is now keyed to the isAutoZoomEnabled value.
    // If it changes, this whole block will re-run to create a new scanner.
    val scanner: GmsBarcodeScanner = remember(isAutoZoomEnabled) {
        Log.d("ScannerInit", "Scanner is being re-initialized with auto-zoom: $isAutoZoomEnabled")

        val optionsBuilder = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_CODE_128, Barcode.FORMAT_CODE_39, Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_CODABAR, Barcode.FORMAT_DATA_MATRIX, Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8, Barcode.FORMAT_ITF, Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E, Barcode.FORMAT_PDF417,
                Barcode.FORMAT_AZTEC
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
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                onNavigateToSettings()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                showAboutDialog = true
                                showMenu = false
                            }
                        )
                    }
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
            Button(onClick = {
                scanner.startScan()
                    .addOnSuccessListener { barcode ->
                        val rawValue = barcode.rawValue
                        val valueType = barcode.valueType
                        //TODO: Format
                        val fullScanResult = "Type: ${getBarcodeFormatName(valueType)}\nValue: ${rawValue ?: "N/A"}"

                        Log.i("BarcodeSuccess", "Barcode raw value: $rawValue, Type: $valueType")
                        Toast.makeText(context, "Barcode Scanned: ${rawValue ?: "No value"}", Toast.LENGTH_LONG).show()

                        // Navigate to results screen instead of starting a new activity
                        onScanSuccess(fullScanResult)
                    }
                    .addOnCanceledListener {
                        Log.i("BarcodeCanceled", "Scan canceled by user.")
                        Toast.makeText(context, "Scan canceled", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("BarcodeFailure", "Scan failed", e)
                        Toast.makeText(context, "Scan failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
            }) {
                Text("Scan")
            }
        }
    }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }
}

private fun getBarcodeFormatName(formatCode: Int): String {
    return when (formatCode) {
        Barcode.TYPE_UNKNOWN -> "TYPE_UNKNOWN"
        Barcode.TYPE_CONTACT_INFO -> "TYPE_CONTACT_INFO"
        Barcode.TYPE_EMAIL -> "TYPE_EMAIL"
        Barcode.TYPE_ISBN -> "TYPE_ISBN"
        Barcode.TYPE_PHONE -> "TYPE_PHONE"
        Barcode.TYPE_PRODUCT -> "TYPE_PRODUCT"
        Barcode.TYPE_SMS -> "TYPE_SMS"
        Barcode.TYPE_TEXT -> "TYPE_TEXT"
        Barcode.TYPE_URL -> "TYPE_URL"
        Barcode.TYPE_WIFI -> "TYPE_WIFI"
        Barcode.TYPE_GEO -> "TYPE_GEO"
        Barcode.TYPE_CALENDAR_EVENT -> "TYPE_CALENDAR_EVENT"
        //TODO: Add Driver's license formatting here.
        Barcode.TYPE_DRIVER_LICENSE -> "TYPE_DRIVER_LICENSE"
        else -> "UNKNOWN_TYPE ($formatCode)"
    }
}

@Composable
private fun AboutDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "About This App") },
        text = {
            Column {
                Text(
                    text = "PDF Barcode Reader",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "By Bill Roth",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Build Time: ${BuildConfig.BUILD_TIME}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text("OK")
                }
            }
        }
    )
}
