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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiroth.simplebarcodescanner.AamvaFormatter
import com.digiroth.simplebarcodescanner.BuildConfig
import com.digiroth.simplebarcodescanner.R
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
 * @param valueType The type of the barcode\'s value (e.g., [Barcode.TYPE_URL], [Barcode.TYPE_TEXT]).
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
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val context: Context = LocalContext.current
    val displayData = formatDisplayData(
        context = context,
        data = scannedData,
        valueType = valueType,
        format = format,
        getBarcodeFormatName = { fmt -> getBarcodeFormatName(fmt) }
    )
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scan_result)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    TopAppBarMenu(
                        settingsText = stringResource(R.string.settings),
                        aboutText = stringResource(R.string.about),
                        historyText = stringResource(R.string.scan_history),
                        menuContentDescText = stringResource(R.string.menu),
                        onSettingsClick = onNavigateToSettings,
                        onAboutClick = { showAboutDialog = true },
                        onHistoryClick = onNavigateToHistory
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
            Text(stringResource(R.string.scanned_code), style = MaterialTheme.typography.headlineSmall)
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
                    ShareUtils.shareText(
                        context = context,
                        text = scannedData, // Share the raw, unformatted data
                        subject = context.getString(
                            R.string.barcode_scan_result,
                            getBarcodeTypeName(context, valueType)
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            ) {
                Text(
                    text = stringResource(R.string.share_result),
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
                    text = stringResource(R.string.scan_again),
                    fontSize = 20.sp
                )
            }
        }
    }

    if (showAboutDialog) {
        val dialogTitle = stringResource(id = R.string.about_dialog_title)
        val fullText = stringResource(R.string.version_info, BuildConfig.VERSION_NAME) +
            "\n" +
            stringResource(R.string.build_time_info, BuildConfig.BUILD_TIME) +
            "\n\n" +
            stringResource(R.string.copyright_notice) +
            "\n\n" +
            stringResource(R.string.license_info) +
            "\n" +
            stringResource(R.string.license_url)

        AboutDialog(
            dialogTitle = dialogTitle,
            dialogText = fullText,
            onDismissRequest = { showAboutDialog = false }
        )
    }
}

private fun getBarcodeTypeName(context: Context, valueType: Int): String {
    return when (valueType) {
        Barcode.TYPE_URL -> context.getString(R.string.barcode_type_url)
        Barcode.TYPE_DRIVER_LICENSE -> context.getString(R.string.barcode_type_drivers_license)
        Barcode.TYPE_TEXT -> context.getString(R.string.barcode_type_text)
        Barcode.TYPE_PRODUCT -> context.getString(R.string.barcode_type_product)
        Barcode.TYPE_ISBN -> context.getString(R.string.barcode_type_isbn)
        Barcode.TYPE_WIFI -> context.getString(R.string.barcode_type_wifi)
        Barcode.TYPE_GEO -> context.getString(R.string.barcode_type_geo)
        Barcode.TYPE_CALENDAR_EVENT -> context.getString(R.string.barcode_type_calendar_event)
        Barcode.TYPE_CONTACT_INFO -> context.getString(R.string.barcode_type_contact_info)
        Barcode.TYPE_EMAIL -> context.getString(R.string.barcode_type_email)
        Barcode.TYPE_PHONE -> context.getString(R.string.barcode_type_phone)
        Barcode.TYPE_SMS -> context.getString(R.string.barcode_type_sms)
        else -> context.getString(R.string.barcode_type_unknown)
    }
}

private fun formatDisplayData(
    context: Context,
    data: String,
    valueType: Int,
    format: Int,
    getBarcodeFormatName: (Int) -> String
): String {
    val typeName = getBarcodeTypeName(context, valueType)
    val formatName = getBarcodeFormatName(format)
    val header = context.getString(R.string.barcode_type_info, typeName, formatName)
    val formattedData = when (valueType) {
        Barcode.TYPE_DRIVER_LICENSE -> AamvaFormatter.formatAamvaData(data)
        else -> data
    }
    return header + formattedData
}
