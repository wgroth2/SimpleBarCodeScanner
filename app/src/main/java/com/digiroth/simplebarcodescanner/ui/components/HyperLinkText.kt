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
    "((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])"
)
private const val URL_TAG = "URL"

/**
 * A Text composable that automatically finds and hyperlinks URLs within a string.
 *
 * @param text The text to display.
 * @param modifier The modifier to be applied to the Text.
 * @param style The base text style to apply.
 * @param linkColor The color of the hyperlink. Defaults to the primary theme color.
 */
@Suppress("DEPRECATION")
@Composable
fun HyperlinkText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    linkColor: Color = MaterialTheme.colorScheme.primary
) {
    val uriHandler = LocalUriHandler.current

    val annotatedString = buildAnnotatedString {
        append(text)

        // Find all URL matches in the text
        val matcher = URL_PATTERN.matcher(text)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val url = matcher.group()

            // Add a style and a string annotation to the URL part
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