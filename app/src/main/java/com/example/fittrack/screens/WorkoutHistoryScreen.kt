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
fun WorkoutHistoryScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel
) {

    val workouts by workoutViewModel.workouts

    LaunchedEffect(Unit) {
        workoutViewModel.loadWorkouts()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        item {
            Text(
                "Workout History",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        items(workouts) { workout ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(workout.exercise)

                    Text("Sets: ${workout.sets}")
                    Text("Reps: ${workout.reps}")
                    Text("Duration: ${workout.duration} min")
                }
            }
        }
    }
}