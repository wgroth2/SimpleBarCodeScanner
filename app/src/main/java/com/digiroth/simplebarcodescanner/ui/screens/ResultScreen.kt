/*
 * Copyright 2025 Bill Roth
 */
package com.digiroth.simplebarcodescanner.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiroth.simplebarcodescanner.AamvaFormatter
import com.digiroth.simplebarcodescanner.ui.components.AboutDialog
import com.digiroth.simplebarcodescanner.ui.components.HyperlinkText
import com.digiroth.simplebarcodescanner.ui.components.TopAppBarMenu
import com.digiroth.simplebarcodescanner.utils.ShareUtils
import com.google.mlkit.vision.barcode.common.Barcode
/**
 * Displays the result of a barcode scan, including its formatted data, type, and format.
 * Provides options to share the scanned data or scan again.
 *
 * @param scannedData The raw data string obtained from the barcode scan.
 * @param valueType The type of the barcode's value (e.g., [Barcode.TYPE_URL], [Barcode.TYPE_TEXT]).
 * @param format The format of the barcode (e.g., [Barcode.FORMAT_QR_CODE], [Barcode.FORMAT_CODE_128]).
 * @param onBack A callback to be invoked when the user wants to navigate back to the scanner.
 * @param onNavigateToSettings A callback to be invoked when the user selects the settings option.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    scannedData: String,
    valueType: Int,
    format: Int,
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context: Context = LocalContext.current
    val displayData = formatDisplayData(data = scannedData, valueType = valueType, format = format)
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Result") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TopAppBarMenu(
                        onSettingsClick = onNavigateToSettings,
                        onAboutClick = { showAboutDialog = true }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Scanned Code:", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            when (valueType) {
                Barcode.TYPE_URL -> {
                    HyperlinkText(
                        text = displayData,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    Text(displayData, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    // Determine if the barcode value is a URL to pass as a hint to ShareUtils
                    val isUrl = valueType == Barcode.TYPE_URL
                    ShareUtils.shareText(
                        context = context,
                        text = scannedData, // Share the raw, unformatted data
                        subject = "Barcode Scan Result (${getBarcodeTypeName(valueType)})",
                        isUrl = isUrl
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            ) {
                Text(
                    text = "Share Result",
                    fontSize = 20.sp
                )
            }

            // Added: Spacer between Share and Scan Again buttons
            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            ) {
                Text(
                    text = "Scan Again",
                    fontSize = 20.sp
                )
            }
        }
    }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }
}

private fun getBarcodeTypeName(valueType: Int): String {
    return when (valueType) {
        Barcode.TYPE_URL -> "URL"
        Barcode.TYPE_DRIVER_LICENSE -> "Driver's License"
        Barcode.TYPE_TEXT -> "Text"
        Barcode.TYPE_PRODUCT -> "Product"
        Barcode.TYPE_ISBN -> "ISBN"
        Barcode.TYPE_WIFI -> "Wi-Fi Network"
        Barcode.TYPE_GEO -> "Geo Point"
        Barcode.TYPE_CALENDAR_EVENT -> "Calendar Event"
        Barcode.TYPE_CONTACT_INFO -> "Contact Info"
        Barcode.TYPE_EMAIL -> "Email"
        Barcode.TYPE_PHONE -> "Phone Number"
        Barcode.TYPE_SMS -> "SMS"
        else -> "Unknown Type"
    }
}

private fun getBarcodeFormatName(format: Int): String {
    return when (format) {
        Barcode.FORMAT_CODE_128 -> "FORMAT_CODE_128"
        Barcode.FORMAT_CODE_39 -> "FORMAT_CODE_39"
        Barcode.FORMAT_CODE_93 -> "FORMAT_CODE_93"
        Barcode.FORMAT_CODABAR -> "FORMAT_CODABAR"
        Barcode.FORMAT_DATA_MATRIX -> "FORMAT_DATA_MATRIX"
        Barcode.FORMAT_EAN_13 -> "FORMAT_EAN_13"
        Barcode.FORMAT_EAN_8 -> "FORMAT_EAN_8"
        Barcode.FORMAT_ITF -> "FORMAT_ITF"
        Barcode.FORMAT_QR_CODE -> "FORMAT_QR_CODE"
        Barcode.FORMAT_UPC_A -> "FORMAT_UPC_A"
        Barcode.FORMAT_UPC_E -> "FORMAT_UPC_E"
        Barcode.FORMAT_PDF417 -> "FORMAT_PDF417"
        Barcode.FORMAT_AZTEC -> "FORMAT_AZTEC"
        else -> "UNKNOWN_FORMAT"
    }
}

private fun formatDisplayData(data: String, valueType: Int, format: Int): String {
    val typeName = getBarcodeTypeName(valueType)
    val formatName = getBarcodeFormatName(format)
    val header = "Type: $typeName ($formatName)\n\n"
    val formattedData = when (valueType) {
        Barcode.TYPE_DRIVER_LICENSE -> AamvaFormatter.formatAamvaData(data)
        else -> data
    }
    return header + formattedData
}
