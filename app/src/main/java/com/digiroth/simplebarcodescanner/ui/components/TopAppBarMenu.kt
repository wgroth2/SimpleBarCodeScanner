/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.digiroth.simplebarcodescanner.R

/**
 * A reusable composable for a dropdown menu in the top app bar.
 *
 * @param settingsText The text to display for the "Settings" menu item.
 * @param aboutText The text to display for the "About" menu item.
 * @param historyText The text to display for the "Scan History" menu item.
 * @param menuContentDescText The content description for the menu icon.
 * @param onSettingsClick A lambda function to be invoked when the "Settings" item is clicked.
 * @param onAboutClick A lambda function to be invoked when the "About" item is clicked.
 * @param onHistoryClick A lambda function to be invoked when the "Scan History" item is clicked.
 */
@Composable
fun TopAppBarMenu(
    settingsText: String,
    aboutText: String,
    historyText: String,
    menuContentDescText: String,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    IconButton(onClick = { menuExpanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = menuContentDescText)
    }

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = { menuExpanded = false }
    ) {
        DropdownMenuItem(
            text = { Text(historyText) },
            onClick = {
                onHistoryClick()
                menuExpanded = false
            }
        )
        DropdownMenuItem(
            text = { Text(settingsText) },
            onClick = {
                onSettingsClick()
                menuExpanded = false
            }
        )
        DropdownMenuItem(
            text = { Text(aboutText) },
            onClick = {
                onAboutClick()
                menuExpanded = false
            }
        )
    }
}
