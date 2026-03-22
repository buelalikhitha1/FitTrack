package com.example.fittrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fittrack.data.AppDatabase
import com.example.fittrack.data.UserDao
import com.example.fittrack.screens.*


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Landing : Screen("landing")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ProfileSetup : Screen("profileSetup")
}


// ViewModelFactory to provide UserDao to AuthViewModel
class AuthViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


// Main navigation host
@Composable
fun AppNavHost() {
    val context = LocalContext.current
    val navController: NavHostController = rememberNavController()

    // Get UserDao from Room
    val userDao = AppDatabase.getDatabase(context).userDao()

    // Inject AuthViewModel using Factory
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userDao)
    )

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        // Sign-Up Screen
        composable(Screen.SignUp.route) {
            SignUpScreen(navController, authViewModel)
        }

        // Profile Setup Screen
        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(navController, authViewModel)
        }

        // Landing Screen
        composable(Screen.Landing.route) {
            LandingScreen(navController, authViewModel)
        }
    }
}