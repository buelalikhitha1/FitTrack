package com.example.fittrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.Screen
import com.example.fittrack.components.BottomNavBar
import com.example.fittrack.data.model.WorkoutEntity
import com.example.fittrack.viewmodels.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel
) {
    // UI state
    val uiState by workoutViewModel.uiState.collectAsState()

    // Load workouts
    LaunchedEffect(Unit) {
        workoutViewModel.loadWorkouts()
    }

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
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // Header title
                        Text(
                            text = "Workout Plans",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        // History button
                        IconButton(
                            onClick = {
                                navController.navigate(Screen.WorkoutHistory.route)
                            }
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = "Workout History",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        },

        // Floating button
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Add workout
                    navController.navigate(Screen.AddWorkout.route)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }

    ) { padding ->

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

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {

                // Instruction text
                Text(
                    text = "Choose a workout to get started",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = workoutViewModel.getCompletionProgress(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE0E0E0)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Workout items
            items(uiState.workouts) { workout ->

                WorkoutCard(
                    workout = workout,
                    // Detail click
                    onClick = {
                        navController.navigate(
                            Screen.WorkoutDetail.createRoute(workout.id)
                        )
                    },
                    // Edit workout
                    onEdit = {
                        navController.navigate(
                            "${Screen.AddWorkout.route}?workoutId=${workout.id}"
                        )
                    },
                    // Delete workout
                    onDelete = {
                        workoutViewModel.deleteWorkout(workout.id)
                    }
                )
            }
        }
    }
}

// Workout card
@Composable
fun WorkoutCard(
    workout: WorkoutEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 6.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface
    ) {

        // Card layout
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Workout name
            Text(
                text = workout.workoutName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Time calculation
            val elapsedMinutes = workout.completedDuration / 60
            val elapsedSeconds = workout.completedDuration % 60
            val totalMinutes = workout.totalDuration / 60
            val totalSeconds = workout.totalDuration % 60

            // Calories calculation
            val dynamicCalories = if (workout.isCompleted) {
                workout.exercises.sumOf {
                    it.durationSeconds * it.caloriesPerMinute / 60
                }
            } else {
                if (workout.totalDuration > 0)
                    workout.exercises.sumOf {
                        it.durationSeconds * it.caloriesPerMinute / 60
                    } * workout.completedDuration / workout.totalDuration
                else 0
            }

            // Duration text
            Text(
                "$elapsedMinutes:${elapsedSeconds.toString().padStart(2, '0')} / " +
                        "$totalMinutes:${totalSeconds.toString().padStart(2, '0')} mins • $dynamicCalories kcal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Completed status
            if (workout.isCompleted) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Edit button
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit")
                }

                // Delete button
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete")
                }
            }
        }
    }
}