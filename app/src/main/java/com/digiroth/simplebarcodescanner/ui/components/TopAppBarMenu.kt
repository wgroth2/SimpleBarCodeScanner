/*
 * Copyright 2025 Bill Roth
 */
package com.digiroth.simplebarcodescanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.digiroth.simplebarcodescanner.BuildConfig
import com.digiroth.simplebarcodescanner.R

@Composable
fun TopAppBarMenu(
    settingsText: String,
    aboutText: String,
    menuContentDescText: String,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    IconButton(onClick = { showMenu = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = menuContentDescText)
    }
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text(settingsText) },
            onClick = {
                onSettingsClick()
                showMenu = false
            }
        )
        DropdownMenuItem(
            text = { Text(aboutText) },
            onClick = {
                onAboutClick()
                showMenu = false
            }
        )
    }
}

@Composable
fun AboutDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.about_this_app)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.pdf_barcode_reader),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.by_bill_roth),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.build_time, BuildConfig.BUILD_TIME),
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
                    Text(stringResource(R.string.ok))
                }
            }
        }
    )
}