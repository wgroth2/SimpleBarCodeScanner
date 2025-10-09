/*
 * Copyright 2025 Bill Roth
 */
package com.digiroth.simplebarcodescanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.digiroth.simplebarcodescanner.data.ScanHistoryDatabase
import com.digiroth.simplebarcodescanner.data.ScanHistoryRepository
import com.digiroth.simplebarcodescanner.data.SettingsRepository
import com.digiroth.simplebarcodescanner.ui.screens.HomeScreen
import com.digiroth.simplebarcodescanner.ui.screens.ResultScreen
import com.digiroth.simplebarcodescanner.ui.screens.ScanHistoryScreen
import com.digiroth.simplebarcodescanner.ui.screens.ScanHistoryViewModel
import com.digiroth.simplebarcodescanner.ui.screens.ScanHistoryViewModelFactory
import com.digiroth.simplebarcodescanner.ui.screens.SettingsScreen
import com.digiroth.simplebarcodescanner.ui.screens.SettingsViewModel
import com.digiroth.simplebarcodescanner.ui.screens.SettingsViewModelFactory
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Defines the navigation routes for the application.
 * Each object represents a different screen.
 */
sealed class Screen(val route: String) {
    /** The main screen with the scan button. */
    object Home : Screen("home_screen")
    /** The settings screen. */
    object Settings : Screen("settings_screen")
    /** The scan history screen. */
    object History : Screen("history_screen")
    /** The screen that displays the result of a scan. Requires scan data as arguments. */
    object Results : Screen("results_screen/{scanResult}/{valueType}/{format}") {
        /**
         * Creates a valid navigation route to the Results screen with the provided scan data.
         * The scan result is URL-encoded to ensure safe transit.
         *
         * @param scanResult The raw string data from the barcode.
         * @param valueType The integer code for the barcode's value type.
         * @param format The integer code for the barcode's format.
         * @return A formatted string representing the navigation route.
         */
        fun createRoute(scanResult: String, valueType: Int, format: Int): String {
            val encodedResult = URLEncoder.encode(scanResult, "UTF-8")
            return "results_screen/$encodedResult/$valueType/$format"
        }
    }
}

/**
 * The main navigation component for the app.
 *
 * This composable sets up the NavHost and defines the navigation graph, including all
 * screens and the logic for navigating between them. It also initializes and provides
 * the necessary ViewModels and repositories to the screens that need them.
 *
 * @param onLanguageChange A lambda function that is triggered when the user changes the app language.
 */
@Composable
fun AppNavigation(onLanguageChange: (String) -> Unit) {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current

    // Settings Dependencies
    val settingsRepository = remember { SettingsRepository(context) }
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(settingsRepository)
    )

    // Scan History Dependencies
    val scanHistoryRepository = remember {
        val database = ScanHistoryDatabase.getDatabase(context)
        ScanHistoryRepository(database.scanHistoryDao())
    }
    val scanHistoryViewModel: ScanHistoryViewModel = viewModel(
        factory = ScanHistoryViewModelFactory(scanHistoryRepository)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onScanSuccess = { result, valueType, format ->
                    navController.navigate(Screen.Results.createRoute(result, valueType, format))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                viewModel = settingsViewModel,
                scanHistoryRepository = scanHistoryRepository
            )
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(
                navArgument("scanResult") { type = NavType.StringType },
                navArgument("valueType") { type = NavType.IntType },
                navArgument("format") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val encodedResult = backStackEntry.arguments?.getString("scanResult") ?: ""
            val scanResult = URLDecoder.decode(encodedResult, "UTF-8")
            val valueType = backStackEntry.arguments?.getInt("valueType") ?: -1
            val format = backStackEntry.arguments?.getInt("format") ?: -1

            ResultScreen(
                scannedData = scanResult,
                valueType = valueType,
                format = format,
                onBack = { navController.popBackStack() },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                viewModel = settingsViewModel,
                onLanguageChange = onLanguageChange
            )
        }

        composable(route = Screen.History.route) {
            ScanHistoryScreen(
                onBack = { navController.popBackStack() },
                onScanHistoryItemClick = { scan ->
                    navController.navigate(
                        Screen.Results.createRoute(
                            scan.rawValue,
                            scan.valueType,
                            scan.format
                        )
                    )
                },
                viewModel = scanHistoryViewModel
            )
        }
    }
}
