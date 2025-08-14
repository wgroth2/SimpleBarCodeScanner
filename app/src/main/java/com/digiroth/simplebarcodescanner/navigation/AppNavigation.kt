package com.digiroth.simplebarcodescanner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.digiroth.simplebarcodescanner.ui.screens.HomeScreen
import com.digiroth.simplebarcodescanner.ui.screens.ResultScreen
import com.digiroth.simplebarcodescanner.ui.screens.SettingsScreen
import java.net.URLDecoder
import java.net.URLEncoder

// 1. Define your screen routes in a type-safe way
sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Settings : Screen("settings_screen")
    object Results : Screen("results_screen/{scanResult}") {
        // Helper function to create the route with the data, ensuring it's URL-encoded
        fun createRoute(scanResult: String): String {
            val encodedResult = URLEncoder.encode(scanResult, "UTF-8")
            return "results_screen/$encodedResult"
        }
    }
}

// 2. Define the Navigation Graph
@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // --- Home Screen ---
        composable(route = Screen.Home.route) {
            HomeScreen(
                onScanSuccess = { result ->
                    navController.navigate(Screen.Results.createRoute(result))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // --- Results Screen ---
        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("scanResult") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedResult = backStackEntry.arguments?.getString("scanResult") ?: ""
            val scanResult = URLDecoder.decode(encodedResult, "UTF-8")

            ResultScreen(
                scannedData = scanResult,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Settings Screen ---
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}