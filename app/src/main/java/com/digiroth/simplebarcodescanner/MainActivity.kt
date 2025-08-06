package com.digiroth.simplebarcodescanner

// from Google Play Lib. Idea from here: https://developers.google.com/ml-kit/vision/barcode-scanning/code-scanner
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.digiroth.simplebarcodescanner.ui.settings.SettingsActivity
import com.digiroth.simplebarcodescanner.ui.theme.PDF417BarCodeScannerTheme
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : ComponentActivity() {
    private lateinit var codeScanner: GmsBarcodeScanner
    // State to hold the scanned value to display in the UI (optional)
    private var scannedValue by mutableStateOf<String?>("No barcode scanned yet")
    // State for showing the About dialog
    private var showAboutDialogState by mutableStateOf(false) // Renamed to avoid conflict if MainScreen is inner

    val context: Context = this // Or applicationContext, requireContext(), etc.

    /**
     * Translates a numeric barcode format code into its textual representation.
     *
     * @param formatCode The integer value of the barcode format (e.g., Barcode.FORMAT_QR_CODE).
     * @return The string name of the format (e.g., "FORMAT_QR_CODE"), or "UNKNOWN_FORMAT" if not recognized.
     */
    fun getBarcodeFormatName(formatCode: Int): String {
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
            Barcode.TYPE_GEO -> "TYPE_GEO" // Geographic coordinates
            Barcode.TYPE_CALENDAR_EVENT -> "TYPE_CALENDAR_EVENT"
            Barcode.TYPE_DRIVER_LICENSE -> "TYPE_DRIVER_LICENSE"
            // Note: Some formats might implicitly map to a certain type.
            // For example, a Barcode.FORMAT_QR_CODE (format) could contain data of Barcode.TYPE_URL (valueType).
            // The GMS scanner often gives you more specific TYPE codes directly from barcode.valueType
            else -> "UNKNOWN_TYPE ($formatCode)"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        // The key must match what you defined in res/xml/preferences.xml
        val autoZoomKey = "auto_zoom"
        // The default value should match what you set in app:defaultValue in preferences.xml
        val defaultAutoZoomValue = true // Assuming you set app:defaultValue="true"
        val isAutoZoomEnabled: Boolean = sharedPreferences.getBoolean(autoZoomKey, defaultAutoZoomValue)

        val gmsScannerOptionsBuilder = GmsBarcodeScannerOptions.Builder()
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
                Barcode.FORMAT_AZTEC
            ).apply { // Use 'apply' to operate on the builder instance (this)
                if (isAutoZoomEnabled) {
                    enableAutoZoom()
                } else {
                    // Assuming the disable method is named disableAutoZoom()
                    // Please verify the actual method name from the GmsBarcodeScannerOptions.Builder documentation
                    // For example, if there's no specific disable method,
                    // you might simply *not* call enableAutoZoom().
                    // If a specific disable method exists, call it here.
                    // disableAutoZoom()
                }
            }
        val gmsScannerOptions = gmsScannerOptionsBuilder.build()

        codeScanner = GmsBarcodeScanning.getClient(this, gmsScannerOptions)

        enableEdgeToEdge()
        setContent {
            PDF417BarCodeScannerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        showAboutDialog = showAboutDialogState,
                        onShowAboutDialogChange = { newState ->       // Pass a lambda to update the state
                            showAboutDialogState = newState
                        },

                                onScanButtonClick = {
                            // Launch CameraActivity HERE Set launch of camera activity
                            Log.d("MainActivity", "Scan button clicked, starting scan...")

                            codeScanner.startScan()
                                .addOnSuccessListener { barcode ->
                                    // Task completed successfully
                                    val rawValue = barcode.rawValue
                                    val valueType = barcode.valueType
                                    // Construct the string you want to send
                                    val fullScanResult = "Type: ${getBarcodeFormatName(valueType)}\nValue: ${rawValue ?: "N/A"}"

                                    val scannedValueForToast = fullScanResult // Update if you still use this for Toast/Log


                                    // --- LAUNCH RESULTS ACTIVITY ---
                                    // Use the newIntent helper from ResultsActivity
                                    val intent = ResultsActivity.newIntent(this, fullScanResult)
                                    startActivity(intent)
                                    // --- END LAUNCH ---

                                    // ... rest of your when block for logging ...

                                    // Update the state with the scanned value
                                    scannedValue = "Type: ${barcode.valueType}\nValue: ${rawValue ?: "N/A"}"
                                    Log.i("BarcodeSuccess", "Barcode raw value: $rawValue, Type: $valueType")
                                    Toast.makeText(this, "Barcode Scanned: ${rawValue ?: "No value"}", Toast.LENGTH_LONG).show()

                                    // You can do more here based on the barcode type or value
                                    when (valueType) {
                                        Barcode.TYPE_WIFI -> {
                                            val ssid = barcode.wifi?.ssid
                                            val password = barcode.wifi?.password
                                            Log.i("BarcodeSuccess", "WiFi SSID: $ssid")
                                        }
                                        Barcode.TYPE_URL -> {
                                            val url = barcode.url?.url
                                            Log.i("BarcodeSuccess", "URL: $url")
                                            // Maybe launch a browser or show the URL
                                        }
                                        Barcode.TYPE_TEXT -> {
                                            Log.i("BarcodeSuccess", "Text: ${barcode.rawValue}")

                                        }
                                        Barcode.TYPE_CONTACT_INFO -> {
                                            Log.i("BarcodeSuccess", "Contact Info: ${barcode.contactInfo}")
                                        }
                                        Barcode.TYPE_EMAIL -> {
                                            Log.i("BarcodeSuccess", "Email: ${barcode.email}")
                                        }
                                        Barcode.TYPE_DRIVER_LICENSE -> {
                                            Log.i("BarcodeSuccess", "Driver License: ${barcode.contactInfo}")
                                        }
                                        // Handle other types as needed
                                        else -> {
                                            Log.i("BarcodeSuccess", "Unknown Barcode type: $valueType, Data: $rawValue")
                                        }
                                    }
                                }
                                .addOnCanceledListener {
                                    // Task canceled by the user
                                    scannedValue = "Scan canceled by user."
                                    Log.i("BarcodeCanceled", "Scan canceled by user.")
                                    Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    // Task failed with an exception
                                    scannedValue = "Scan failed: ${e.message}"
                                    Log.e("BarcodeFailure", "Scan failed", e)
                                    Toast.makeText(this, "Scan failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }

                        }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(
        showAboutDialog: Boolean,                     // Parameter to receive the state
        onShowAboutDialogChange: (Boolean) -> Unit,   // Parameter for the callback
        // Removed imageUri and onClearImageClick as they are not needed for this version
        onScanButtonClick: () -> Unit
    ) {
        var showMenu by remember { mutableStateOf(false) } // Keep menu functionality if desired

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("PDF Barcode Reader") }, // Or your desired title
                    actions = {
                        // You can keep or remove the menu based on your needs
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
                                    val intent =
                                        Intent(this@MainActivity, SettingsActivity::class.java)
                                    startActivity(intent)
                                    showMenu = false // if you have a menu state variable
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("About") },
                                onClick = {
                                    onShowAboutDialogChange(true) // Show the dialogs
                                    showMenu = false }
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box( // Use Box for simple centering
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center // Center the button in the Box
            ) {
                Button(onClick = onScanButtonClick) {
                    Text("Scan")
                }
            }
        }
        // *** THIS IS THE FIX: Conditionally display the About Dialog ***
        if (showAboutDialog) {
            AboutDialog(
                onDismissRequest = {
                    onShowAboutDialogChange(false) // Hide the dialog when it's dismissed
                }
            )
        }
        // *** END OF FIX ***
    }
    // Moved AboutDialog to be callable from MainActivity's scope or be a top-level function
    @Composable
    fun AboutDialog(
        onDismissRequest: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = "About This App")
            },
            text = {
                Column {
                    Text(
                        text = "PDF Barcode Reader", // Or your actual app name
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
                    // Example: Displaying app version from BuildConfig
                    // Make sure to add this to your build.gradle's defaultConfig if you want it
                    // android { defaultConfig { ... versionName "1.0" ... } }
                    // Text(text = "Version: ${com.digiroth.pdf417barcodescanner.BuildConfig.VERSION_NAME}")
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text("OK")
                    }
                }
            }
            // By default, AlertDialog is modal in the sense that you can't interact behind it.
            // It's dismissed by the confirmButton, dismissButton (if present), or back press.
            // It is NOT dismissed by clicking outside the dialog bounds.
        )
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreviewMainScreenWithAboutDialog() {
        var showDialog by remember { mutableStateOf(true) } // Start with dialog shown for preview
        PDF417BarCodeScannerTheme {
            // In a real preview, you might need to mock the activity context for intent,
            // or simplify MainScreen for preview purposes.
            // For this specific preview, we just want to see the dialog.
            if (showDialog) {
                AboutDialog(onDismissRequest = { showDialog = false })
            } else {
                Text("Dialog is hidden. Re-run preview with showDialog = true to see it.")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreviewMainScreenSimple() { // Renamed preview for clarity
        var showDialog by remember { mutableStateOf(false) }
        PDF417BarCodeScannerTheme {
            MainScreen(
                onScanButtonClick = {},
                showAboutDialog = showDialog,
                onShowAboutDialogChange = {showDialog = it}
            )
        }
    }
} // main activity end.
