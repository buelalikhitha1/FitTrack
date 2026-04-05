package com.example.fittrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.viewmodels.WorkoutViewModel

@Composable
fun WorkoutPlanScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel
) {

    val workoutList = listOf(
        "Push Ups",
        "Squats",
        "Plank",
        "Running",
        "Cycling",
        "Jump Rope"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                "Workout Plan",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        items(workoutList) { exercise ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        exercise,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            workoutViewModel.addWorkout(
                                exercise,
                                3,
                                12,
                                10
                            )
                        }
                    ) {
                        Text("Log Workout")
                    }
                }
            }
        }
    }
}