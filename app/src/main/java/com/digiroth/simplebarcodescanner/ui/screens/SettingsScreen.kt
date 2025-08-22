package com.digiroth.simplebarcodescanner.ui.screens

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.preference.PreferenceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SwitchPreference(
                key = "auto_zoom",
                title = "Enable Auto-Zoom",
                summary = "Automatically zoom in on barcodes",
                defaultValue = true
            )
        }
    }
}

/**
 * A composable that displays a title, a summary, and a switch.
 * It manages its own state by reading and writing to SharedPreferences.
 */
@Composable
private fun SwitchPreference(
    key: String,
    title: String,
    summary: String,
    defaultValue: Boolean
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    // State for the switch, initialized from SharedPreferences
    var isChecked by remember {
        mutableStateOf(sharedPreferences.getBoolean(key, defaultValue))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Update state and SharedPreferences on click
                val newCheckedState = !isChecked
                isChecked = newCheckedState
                sharedPreferences
                    .edit()
                    .putBoolean(key, newCheckedState)
                    .apply()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { newCheckedState ->
                // This lambda is called when the user drags the switch thumb.
                isChecked = newCheckedState
                sharedPreferences
                    .edit()
                    .putBoolean(key, newCheckedState)
                    .apply()
            }
        )
    }
}
