package com.example.fittrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fittrack.api.ApiClient
import com.example.fittrack.data.AppDatabase
import com.example.fittrack.data.dao.FoodDao
import com.example.fittrack.data.dao.UserDao
import com.example.fittrack.screens.*
import com.example.fittrack.viewmodels.*

// Screen Routes
sealed class Screen(val route: String) {

    object Splash : Screen("splash")
    object Landing : Screen("landing")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ProfileSetup : Screen("profileSetup")
    object LogFood : Screen("logFood")
}

@Composable
fun AppNavHost(apiKey: String) {

    val context = LocalContext.current
    val navController: NavHostController = rememberNavController()

    // get User and Food DAO instance
    val userDao: UserDao = AppDatabase.getDatabase(context).userDao()
    val foodDao: FoodDao = AppDatabase.getDatabase(context).foodDao()

    // auth viewmodel initialization
    val authViewModel: AuthViewModel =
        viewModel(factory = AuthViewModelFactory(userDao))

    // food viewmodel initialization
    val foodViewModel: FoodViewModel =
        viewModel(factory = FoodViewModelFactory(foodDao, ApiClient.api))

    // navigation graph setup
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // Splash screen Navigation
        composable(Screen.Splash.route) {
            SplashScreen(navController) // open
        }

        // Login screen Navigation
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        // Signup screen Navigation
        composable(Screen.SignUp.route) {
            SignUpScreen(navController, authViewModel)
        }

        // Profile setup Navigation
        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(navController, authViewModel)
        }

        // Landing screen Navigation
        composable(Screen.Landing.route) {
            LandingScreen(navController, authViewModel)
        }

        // LogFood Screen Navigation
        composable(Screen.LogFood.route) {
            LogFoodScreen(navController, foodViewModel, apiKey)
        }
    }
}