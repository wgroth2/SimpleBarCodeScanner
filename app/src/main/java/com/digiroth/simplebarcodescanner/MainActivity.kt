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
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.digiroth.simplebarcodescanner.data.SettingsRepository
import com.digiroth.simplebarcodescanner.navigation.AppNavigation
import com.digiroth.simplebarcodescanner.ui.theme.SimpleBarCodeScannerTheme
import kotlinx.coroutines.flow.first
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val settingsRepository = remember { SettingsRepository(applicationContext) }
            var currentLanguage by remember { mutableStateOf("en") }

            LaunchedEffect(Unit) {
                currentLanguage = settingsRepository.appLanguage.first()
            }

            val localizedContext = remember(currentLanguage) {
                updateLocale(this, currentLanguage)
            }

            CompositionLocalProvider(LocalContext provides localizedContext) {
                SimpleBarCodeScannerTheme {
                    AppNavigation(
                        onLanguageChange = { newLanguageCode ->
                            currentLanguage = newLanguageCode
                        }
                    )
                }
            }
        }
    }
}

private fun updateLocale(context: Context, languageCode: String): Context {
    val locale = Locale.forLanguageTag(languageCode)
    Locale.setDefault(locale)

    val configuration = Configuration(context.resources.configuration)
    configuration.setLocales(LocaleList(locale))

    return context.createConfigurationContext(configuration)
}
