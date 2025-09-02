/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Utility object for handling system-wide sharing of text data.
 */
object ShareUtils {

    /**
     * Shares the given text data using a system-wide share intent.
     * The user will be prompted to choose an app to share the content with.
     *
     * @param context The [Context] used to start the activity.
     * @param text The text content to be shared. This will typically be the barcode's raw value.
     * @param subject An optional subject line, useful for email apps (e.g., "Barcode Scan Result").
     * @param isUrl A boolean indicating if the shared text is a URL. Setting this can sometimes
     *              hint to sharing apps how to best handle the content, though "text/plain" is
     *              generally sufficient for a string representation of a URL.
     */
    fun shareText(context: Context, text: String, subject: String = "Barcode Scan Result", isUrl: Boolean = false) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain" // Most versatile for sharing text and URLs as strings
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            // Optionally, if it's a URL, you could also set Intent.DATA, but EXTRA_TEXT is primary for ACTION_SEND
            if (isUrl) {
                try {
                    data = Uri.parse(text)
                } catch (e: Exception) {
                    // Log or handle malformed URI if necessary, but EXTRA_TEXT will still be there
                }
            }
        }

        // Always use createChooser to ensure the user can select an app from the system dialog
        val chooserIntent = Intent.createChooser(shareIntent, "Share barcode result via")

        // It's good practice to check if there's an activity to handle the intent
        if (shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(chooserIntent)
        }
    }
}