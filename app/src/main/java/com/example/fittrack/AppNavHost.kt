package com.example.fittrack

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fittrack.screens.LandingScreen
import com.example.fittrack.screens.SplashScreen

// App screens with routes
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Landing : Screen("landing")
}

@Composable
fun AppNavHost() {
    // Creates navigation controller
    val navController: NavHostController = rememberNavController()

    // Start navigation with first screen
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        // Splash screen
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        // Landing screen
        composable(Screen.Landing.route) {
            LandingScreen()
        }
    }
}