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
import com.digiroth.simplebarcodescanner.data.SettingsRepository
import com.digiroth.simplebarcodescanner.ui.screens.HomeScreen
import com.digiroth.simplebarcodescanner.ui.screens.ResultScreen
import com.digiroth.simplebarcodescanner.ui.screens.SettingsScreen
import com.digiroth.simplebarcodescanner.ui.screens.SettingsViewModel
import com.digiroth.simplebarcodescanner.ui.screens.SettingsViewModelFactory
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Settings : Screen("settings_screen")
    object Results : Screen("results_screen/{scanResult}/{valueType}/{format}") {
        fun createRoute(scanResult: String, valueType: Int, format: Int): String {
            val encodedResult = URLEncoder.encode(scanResult, "UTF-8")
            return "results_screen/$encodedResult/$valueType/$format"
        }
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository(context) }
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(settingsRepository)
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
                viewModel = settingsViewModel
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
                }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                viewModel = settingsViewModel
            )
        }
    }
}
