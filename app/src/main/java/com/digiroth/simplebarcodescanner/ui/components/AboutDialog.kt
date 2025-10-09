/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.digiroth.simplebarcodescanner.R

/**
 * A dialog that displays information about the application.
 *
 * This composable now accepts pre-resolved strings to ensure it works correctly
 * with dynamic language changes, as dialogs may not inherit the correct
 * CompositionLocal context.
 *
 * @param dialogTitle The resolved title string for the dialog.
 * @param dialogText The resolved body text for the dialog.
 * @param onDismissRequest A lambda function to be invoked when the dialog is dismissed.
 */
@Composable
fun AboutDialog(
    dialogTitle: String,
    dialogText: String,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = dialogTitle)
        },
        text = {
            HyperlinkText(text = dialogText)
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.ok))
            }
        }
    )
}
