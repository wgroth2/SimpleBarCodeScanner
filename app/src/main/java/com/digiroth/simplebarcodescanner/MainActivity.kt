/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.digiroth.simplebarcodescanner.data.SettingsRepository
import com.digiroth.simplebarcodescanner.navigation.AppNavigation
import com.digiroth.simplebarcodescanner.ui.theme.SimpleBarCodeScannerTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val settingsRepository = remember { SettingsRepository(applicationContext) }
            val scope = rememberCoroutineScope()

            var currentLanguage by remember { mutableStateOf<String?>(null) }

            // This effect runs once to load the initial language from the repository.
            LaunchedEffect(Unit) {
                currentLanguage = settingsRepository.appLanguage.first()
            }

            // The UI is only displayed after the language has been loaded.
            currentLanguage?.let { lang ->
                // A new context is created whenever the language changes.
                val localizedContext = remember(lang) {
                    createLocaleContext(this, lang)
                }

                // This provides the specially configured context to the entire UI tree.
                CompositionLocalProvider(LocalContext provides localizedContext) {
                    SimpleBarCodeScannerTheme {
                        AppNavigation(
                            onLanguageChange = { newLanguageCode ->
                                // When the user selects a new language:
                                scope.launch {
                                    // 1. First, save the new preference to DataStore.
                                    settingsRepository.setAppLanguage(newLanguageCode)
                                    // 2. Then, update the state to trigger a recomposition with the new context.
                                    currentLanguage = newLanguageCode
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Creates a new Context with a specific language configuration.
 */
@Suppress("DEPRECATION")
// TODO: Fix deprecation somedaY
private fun createLocaleContext(context: Context, languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale) // Set default locale for consistency

    val configuration = Configuration(context.resources.configuration)
    val localeList = LocaleList(locale)
    configuration.setLocales(localeList)

    return context.createConfigurationContext(configuration)
}
