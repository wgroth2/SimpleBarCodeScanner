/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import java.util.regex.Pattern

// A regex pattern to find URLs in a string.
private val URL_PATTERN: Pattern = Pattern.compile(
    "((https?|ftp|file)://[-a-zA-Z0-g+&@#/%?=~_|!:,.;]*[-a-zA-Z0-g+&@#/%=~_|])"
)
private const val URL_TAG = "URL"

/**
 * A Text composable that automatically finds and hyperlinks URLs within a string.
 *
 * It applies the theme's default body style and uses the primary color for links.
 *
 * @param text The text to display.
 * @param modifier The modifier to be applied to the Text.
 * @param style The base text style to apply. Defaults to the bodyLarge style from the current theme.
 * @param linkColor The color of the hyperlink. Defaults to the primary theme color.
 */
@Suppress("DEPRECATION")
@Composable
fun HyperlinkText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    linkColor: Color = MaterialTheme.colorScheme.primary
) {
    val uriHandler = LocalUriHandler.current
    // Explicitly use the theme's "onSurface" color for all regular text.
    // This ensures visibility in both light and dark modes.
    val textColor = MaterialTheme.colorScheme.onSurface

    val annotatedString = buildAnnotatedString {
        // 1. Apply the theme-aware text color to the ENTIRE string first.
        pushStyle(style.toSpanStyle().copy(color = textColor))
        append(text)
        pop()

        // 2. Find any URLs in the text.
        val matcher = URL_PATTERN.matcher(text)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val url = matcher.group()

            // 3. Add a specific style for ONLY the URL part.
            // This overrides the base color set in step 1 for the link's range.
            addStyle(
                style = SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                ),
                start = start,
                end = end
            )
            addStringAnnotation(
                tag = URL_TAG,
                annotation = url,
                start = start,
                end = end
            )
        }
    }

    // Use ClickableText for handling clicks on annotated strings
    ClickableText(
        text = annotatedString,
        style = style,
        modifier = modifier,
        onClick = { offset ->
            // Find the annotation at the clicked offset
            annotatedString.getStringAnnotations(
                tag = URL_TAG,
                start = offset,
                end = offset
            ).firstOrNull()?.let { annotation ->
                // If found, open the URI
                uriHandler.openUri(annotation.item)
            }
        }
    )
}
