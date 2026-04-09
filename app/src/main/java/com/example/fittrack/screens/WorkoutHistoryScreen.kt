package com.example.fittrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.Screen
import com.example.fittrack.components.BottomNavBar
import com.example.fittrack.viewmodels.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHistoryScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel
) {

    // UI state
    val uiState by workoutViewModel.uiState.collectAsState()
    val completedWorkouts = uiState.workouts.filter { it.isCompleted }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // Bottom navbar
        bottomBar = {
            BottomNavBar(navController)
        },
        // Top header
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .padding(vertical = 24.dp, horizontal = 20.dp)
                ) {
                    // Status spacing
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                WindowInsets.statusBars
                                    .asPaddingValues()
                                    .calculateTopPadding()
                            )
                    )
                    // Header row
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back button
                        IconButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        // Header title
                        Text(
                            text = "Workout History",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    ) { padding ->
        // Empty state
        if (completedWorkouts.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {

                // Empty text
                Text(
                    text = "No completed workouts yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {
            // Workout list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),

                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Workout items
                items(completedWorkouts) { workout ->

                    // Date format
                    val dateStr = SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    ).format(Date(workout.date))

                    // Time calculation
                    val completedMinutes = workout.completedDuration / 60
                    val completedSeconds = workout.completedDuration % 60
                    val totalMinutes = workout.totalDuration / 60
                    val totalSeconds = workout.totalDuration % 60

                    // Calories calculation
                    val dynamicCalories =
                        workout.exercises.sumOf {
                            it.durationSeconds * it.caloriesPerMinute / 60
                        }

                    // Workout card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                // Navigate detail screen
                                navController.navigate(
                                    Screen.WorkoutDetail.createRoute(workout.id)
                                )
                            },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {

                        // Card layout
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {

                            // Workout name
                            Text(
                                workout.workoutName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Time row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Time",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "$completedMinutes:${completedSeconds.toString().padStart(2, '0')} / " +
                                            "$totalMinutes:${totalSeconds.toString().padStart(2, '0')} mins"
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Calories row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Calories",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "$dynamicCalories kcal"
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Date row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Date",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(dateStr)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Status text
                            Text(
                                text = "Completed",
                                color = Color(0xFF4CAF50),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}