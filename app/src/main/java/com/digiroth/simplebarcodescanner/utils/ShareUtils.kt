/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.utils

import android.content.Context
import android.content.Intent
import com.digiroth.simplebarcodescanner.R

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

     */
    fun shareText(context: Context, text: String, subject: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain" // Most versatile for sharing text and URLs as strings
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        // Always use createChooser to ensure the user can select an app from the system dialog.
        // The chooser will gracefully handle cases where no app can handle the intent, so we don't
        // need to call resolveActivity() beforehand, which can be problematic on some devices (e.g., Samsung).
        val chooserIntent = Intent.createChooser(shareIntent, context.getString(R.string.share_barcode_result_via))
        context.startActivity(chooserIntent)
    }
}