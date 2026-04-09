package com.example.fittrack

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fittrack.api.ApiClient
import com.example.fittrack.data.AppDatabase
import com.example.fittrack.data.dao.FoodDao
import com.example.fittrack.data.dao.UserDao
import com.example.fittrack.data.dao.WorkoutDao
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
    object Workout : Screen("workout")
    object WorkoutDetail : Screen("workout_detail/{workoutId}") {
        fun createRoute(id: Int) = "workout_detail/$id"
    }
    object AddWorkout : Screen("add_workout")
    object WorkoutHistory : Screen("workout_history")
    object Profile : Screen("profile")
    object FoodHistory : Screen("food_history")
}

// App Navigation
@Composable
fun AppNavHost(apiKey: String) {

    // Context
    val context = LocalContext.current
    val navController: NavHostController = rememberNavController()

    // Database
    val database = AppDatabase.getDatabase(context)
    val userDao: UserDao = database.userDao()
    val foodDao: FoodDao = database.foodDao()
    val workoutDao: WorkoutDao = database.workoutDao()

    // ViewModels
    val application = LocalContext.current.applicationContext as Application

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(application, userDao)
    )

    val foodViewModel: FoodViewModel =
        viewModel(factory = FoodViewModelFactory(foodDao, ApiClient.api))

    val workoutViewModel: WorkoutViewModel =
        viewModel(factory = WorkoutViewModelFactory(workoutDao))

    // Navigation Graph
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // Splash Screen Navigation
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        // Login Screen Navigation
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        // Signup Screen Navigation
        composable(Screen.SignUp.route) {
            SignUpScreen(navController, authViewModel)
        }

        // Profile Setup Screen Navigation
        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(navController, authViewModel)
        }

        // Landing Screen Navigation
        composable(Screen.Landing.route) {
            LandingScreen(navController, authViewModel, foodViewModel, workoutViewModel)
        }

        // Log Food Navigation
        composable(Screen.LogFood.route) {
            LogFoodScreen(navController, foodViewModel, apiKey)
        }

        // Profile Navigation
        composable(Screen.Profile.route) {
            ProfileScreen(navController, authViewModel)
        }

        // Food History Screen Navigation
        composable(Screen.FoodHistory.route) {
            FoodHistoryScreen(navController, foodViewModel)
        }

        // Workout Screen Navigation
        composable(Screen.Workout.route) {
            WorkoutScreen(
                navController = navController,
                workoutViewModel = workoutViewModel
            )
        }

        // Add/Edit Workout Navigation
        composable(
            route = Screen.AddWorkout.route + "?workoutId={workoutId}",
            arguments = listOf(navArgument("workoutId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("workoutId")?.takeIf { it != -1 }
            AddWorkoutScreen(
                navController = navController,
                workoutViewModel = workoutViewModel,
                workoutId = workoutId
            )
        }

        // Workout Detail Navigation
        composable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: 0
            WorkoutDetailScreen(
                navController = navController,
                workoutId = workoutId,
                workoutViewModel = workoutViewModel
            )
        }

        // Workout History Navigation
        composable(Screen.WorkoutHistory.route) {
            WorkoutHistoryScreen(
                navController = navController,
                workoutViewModel = workoutViewModel
            )
        }
    }
}