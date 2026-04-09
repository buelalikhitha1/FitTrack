package com.example.fittrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.Screen
import com.example.fittrack.components.BottomNavBar
import com.example.fittrack.viewmodels.AuthViewModel
import com.example.fittrack.viewmodels.FoodViewModel
import com.example.fittrack.viewmodels.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LandingScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel,
    workoutViewModel: WorkoutViewModel
) {

    // Profile state
    val profile by authViewModel.userProfile
    val recentFoods by foodViewModel.recentFoods
    val workoutState by workoutViewModel.uiState.collectAsState()

    // Load data
    LaunchedEffect(Unit) {
        workoutViewModel.loadWorkouts()
        authViewModel.loadUserProfile()
    }

    // Calories calculation
    val todayCalories = recentFoods.sumOf { it.calories }
    val workoutMinutes = workoutState.workouts.sumOf { it.totalDuration } / 60

    // Burned calories
    val burnedCalories = workoutState.workouts.sumOf { workout ->
        if (workout.isCompleted) {
            workout.exercises.sumOf { it.durationSeconds * it.caloriesPerMinute / 60 }
        } else {
            if (workout.totalDuration > 0)
                workout.exercises.sumOf { it.durationSeconds * it.caloriesPerMinute / 60 } *
                        workout.completedDuration / workout.totalDuration
            else 0
        }
    }

    // Target calories
    val targetCalories = profile?.let {
        val weight = it.weight.toDouble()
        val height = it.height.toDouble()
        val age = it.age.toDouble()
        val bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5
        when (it.goal.lowercase()) {
            "lose weight" -> bmr - 300
            "gain weight" -> bmr + 300
            else -> bmr
        }
    } ?: 2000.0

    // Remaining calories
    val remainingCalories = (targetCalories - todayCalories).coerceAtLeast(0.0)

    // Current date
    val date = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    // Screen scaffold
    Scaffold(

        // Add food FAB
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.LogFood.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },

        // Bottom navbar
        bottomBar = { BottomNavBar(navController) }

    ) { padding ->

        // Main column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Top header
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Header content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 20.dp)
                ) {
                    Text(
                        text = "Welcome Back To FitTrack Dashboard",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Cards list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {

                // Total calories
                item {
                    SummaryCard(
                        title = "Total Calories",
                        value = "${todayCalories.toInt()} / ${targetCalories.toInt()} cal",
                        subtitle = "Remaining: ${remainingCalories.toInt()} cal",
                        icon = Icons.Default.LocalFireDepartment
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // Workout duration
                item {
                    SummaryCard(
                        title = "Workout Duration",
                        value = "$workoutMinutes min",
                        icon = Icons.Default.FitnessCenter
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // Calories burned
                item {
                    SummaryCard(
                        title = "Calories Burned",
                        value = "${burnedCalories.toInt()} cal",
                        icon = Icons.Default.TrendingDown
                    )
                    Spacer(Modifier.height(20.dp))
                }

                // Quick actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // Log meal
                        Box(modifier = Modifier.weight(1f)) {
                            QuickActionCard(
                                title = "Log Meal",
                                subtitle = "Scan food",
                                icon = Icons.Default.Camera
                            ) { navController.navigate(Screen.LogFood.route) }
                        }

                        // Log workout
                        Box(modifier = Modifier.weight(1f)) {
                            QuickActionCard(
                                title = "Log Workout",
                                subtitle = "Track exercise",
                                icon = Icons.Default.FitnessCenter
                            ) { navController.navigate(Screen.Workout.route) }
                        }
                    }

                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}

// Quick action card
@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    // Card surface
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {

        // Card layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Action icon
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            // Text section
            Column {

                // Title text
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium
                )

                // Subtitle text
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Summary card
@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    subtitle: String? = null
) {

    // Card surface
    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {

        // Card row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Text column
            Column {

                // Title label
                Text(
                    title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                // Value text
                Text(
                    value,
                    style = MaterialTheme.typography.headlineSmall
                )

                // subtitle
                subtitle?.let {
                    Spacer(Modifier.height(4.dp))
                    // Subtitle text
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Icon display
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}