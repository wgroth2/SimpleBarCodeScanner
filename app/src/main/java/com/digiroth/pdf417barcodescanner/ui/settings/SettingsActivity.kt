package com.digiroth.pdf417barcodescanner.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager

// Key for SharedPreferences
const val PREF_AUTO_ZOOM = "auto_zoom" // Matches app:key in preferences.xml
const val PREF_AUTO_ZOOM_DEFAULT = true // Matches app:defaultValue

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // No need for supportActionBar?.setDisplayHomeAsUpEnabled(true) with Compose TopAppBar

        setContent {
            // You can use your app's theme here
            // Assuming PDF417BarCodeScannerTheme is your app's Material 3 theme
            // PDF417BarCodeScannerTheme { // Uncomment if you have your own theme
            MaterialTheme { // Using default MaterialTheme for simplicity
                SettingsScreen(
                    onNavigateUp = {
                        // This replicates NavUtils.navigateUpFromSameTask(this)
                        // or more simply, just finish the activity if it's a simple up.
                        finish()
                    }
                )
            }
            // }
        }
    }

    // You can keep this if your theme provides an ActionBar and you want to use the XML way
    // But for a pure Compose solution, the TopAppBar's navigation icon handles this.
    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Simplified up navigation
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    */
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateUp: () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = remember { PreferenceManager.getDefaultSharedPreferences(context) }

    // State for the Auto Zoom preference
    val autoZoomEnabled = rememberPreference(
        sharedPreferences = sharedPreferences,
        key = PREF_AUTO_ZOOM,
        defaultValue = PREF_AUTO_ZOOM_DEFAULT
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }, // Or use stringResource(R.string.title_activity_settings)
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Up" // Or stringResource for accessibility
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Scanner Settings", // Example section title
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SwitchSettingItem(
                title = "Auto Zoom", // Or use stringResource(R.string.auto_zoom_title)
                summary = "Enable or disable automatic zooming during scan", // Or stringResource
                checked = autoZoomEnabled.value,
                onCheckedChanged = { newValue ->
                    autoZoomEnabled.value = newValue
                }
            )

            // Add more settings items here
            // e.g., OtherSwitchSettingItem(), ListPreferenceItem(), etc.

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "General Settings", // Example section title
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Add other general settings if any
        }
    }
}

/**
 * A reusable Composable for a setting item with a title, summary, and a Switch.
 */
@Composable
fun SwitchSettingItem(
    title: String,
    summary: String?,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChanged
        )
    }
}

/**
 * A helper Composable function to remember and manage a boolean preference.
 */
@Composable
fun rememberPreference(
    sharedPreferences: SharedPreferences,
    key: String,
    defaultValue: Boolean
): MutableState<Boolean> {
    val currentValue = sharedPreferences.getBoolean(key, defaultValue)
    val state = remember { mutableStateOf(currentValue) }

    // Update SharedPreferences when the state changes
    // This is a side effect that should be handled carefully.
    // For more complex scenarios, consider a ViewModel.
    val lastValue by remember { mutableStateOf(currentValue) } // remember initial value

    if (state.value != lastValue) { // only write if it actually changed from last committed value
        LaunchedEffect(state.value) { // Use LaunchedEffect for side effects tied to state.value
            with(sharedPreferences.edit()) {
                putBoolean(key, state.value)
                apply() // Use apply() for asynchronous saving
            }
        }
    }
    return state
}

// --- Previews (Optional but recommended) ---
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(onNavigateUp = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SwitchSettingItemPreview() {
    MaterialTheme {
        SwitchSettingItem(
            title = "Sample Switch",
            summary = "This is a description for the sample switch.",
            checked = true,
            onCheckedChanged = {}
        )
    }
}