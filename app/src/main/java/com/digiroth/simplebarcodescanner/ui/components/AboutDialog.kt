/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.digiroth.simplebarcodescanner.BuildConfig
import com.digiroth.simplebarcodescanner.R

/**
 * A dialog that displays information about the application, including its version,
 * copyright, and license details. It uses the [HyperlinkText] composable to make
 * the license URL clickable.
 *
 * @param onDismissRequest A lambda function to be invoked when the dialog is dismissed,
 *                         either by clicking the confirm button or by clicking outside the dialog.
 */
@Composable
fun AboutDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.about_dialog_title))
        },
        text = {
            // Combine multiple string resources to build the dialog's content.
            // BuildConfig.VERSION_NAME provides the version from the build.gradle file.
            val fullText = stringResource(R.string.version_info, BuildConfig.VERSION_NAME) +
                "\n" +
                stringResource(R.string.build_time_info, BuildConfig.BUILD_TIME) +
                "\n\n" +
                stringResource(R.string.copyright_notice) +
                "\n\n" +
                stringResource(R.string.license_info) +
                "\n" +
                stringResource(R.string.license_url) // The HyperlinkText will handle this URL

            HyperlinkText(text = fullText)
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.ok))
            }
        }
    )
}
