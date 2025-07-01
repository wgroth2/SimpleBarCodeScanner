// ResultsActivity.kt
package com.digiroth.pdf417barcodescanner // Use your actual package name

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digiroth.pdf417barcodescanner.ui.theme.PDF417BarCodeScannerTheme

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
            Text(
                text = scannedData,
                style = MaterialTheme.typography.bodyLarge
            )
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