/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.digiroth.simplebarcodescanner.R
import com.digiroth.simplebarcodescanner.data.Scan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * A composable that displays a single item in the scan history list.
 *
 * It shows the scan's timestamp, barcode format, and a snippet of the raw value.
 * The entire item is clickable, triggering a navigation event.
 *
 * @param scan The [Scan] data object to display.
 * @param onItemClick A lambda function to be invoked when the item is clicked.
 * @param getBarcodeFormatName A function that converts a barcode format integer into a human-readable string.
 */
@Composable
fun ScanHistoryItem(
    scan: Scan,
    onItemClick: (Scan) -> Unit,
    getBarcodeFormatName: (Int) -> String
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    val scanTime = sdf.format(Date(scan.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .clickable { onItemClick(scan) }
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.history_item_time, scanTime),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.history_item_type, getBarcodeFormatName(scan.format)),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.history_item_result, scan.rawValue.take(50)),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
