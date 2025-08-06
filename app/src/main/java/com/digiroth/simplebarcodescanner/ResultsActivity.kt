// ResultsActivity.kt
package com.digiroth.simplebarcodescanner // Use your actual package name

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digiroth.simplebarcodescanner.ui.theme.PDF417BarCodeScannerTheme

class ResultsActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SCAN_RESULT = "com.digiroth.pdf417barcodescanner.SCAN_RESULT"

        fun newIntent(context: Context, scanResult: String): Intent {
            val intent = Intent(context, ResultsActivity::class.java)
            intent.putExtra(EXTRA_SCAN_RESULT, scanResult)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scannedData = intent.getStringExtra(EXTRA_SCAN_RESULT) ?: "No data received"

        setContent {
            PDF417BarCodeScannerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ResultScreen(scannedData = scannedData)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(scannedData: String) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Result") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply padding from Scaffold
                .padding(16.dp) // Additional padding for content
                .verticalScroll(rememberScrollState()), // Makes the Column scrollable
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SelectionContainer {
                Text(
                    text = scannedData,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth() // Good for readability
                    )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val annotatedString = AnnotatedString(scannedData)
                    clipboardManager.setText(annotatedString)
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Copy to Clipboard")
            }
            // Share Button
            Button(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, scannedData)
                        type = "text/plain"
                    }
                    // Create a chooser to let the user pick an app
                    val shareIntent = Intent.createChooser(sendIntent, null) // "null" for default chooser title

                    // Verify that the intent will resolve to an activity
                    if (sendIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(shareIntent)
                    } else {
                        // Handle case where no app can handle the share action
                        Toast.makeText(context, "No app available to share this content", Toast.LENGTH_LONG).show()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Share, // Use the Share icon
                    contentDescription = "Share",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Share")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewResultScreen() {
    PDF417BarCodeScannerTheme {
        ResultScreen("Type: TEXT\nValue: This is a sample of scanned text data that might be quite long and therefore needs to be scrollable to see all of its content.")
    }
}