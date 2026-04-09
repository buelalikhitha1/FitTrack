package com.example.fittrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.components.BottomNavBar
import com.example.fittrack.data.model.Exercise
import com.example.fittrack.viewmodels.WorkoutViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel,
    workoutId: Int? = null
) {
    // State values
    val uiState by workoutViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Form states
    var workoutName by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf(1) }
    var reps by remember { mutableStateOf(1) }
    var minutes by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }

    // Prefill data
    LaunchedEffect(workoutId) {
        workoutId?.let { id ->
            val workout = uiState.workouts.firstOrNull { it.id == id }
            workout?.let { w ->
                workoutName = w.workoutName
                val ex = w.exercises.firstOrNull()
                if (ex != null) {
                    sets = ex.sets
                    reps = ex.reps
                    minutes = ex.durationSeconds / 60
                    seconds = ex.durationSeconds % 60
                }
            }
        }
    }

    // Screen layout
    Scaffold(
        // Bottom navbar
        bottomBar = { BottomNavBar(navController) },
        // Top header
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // spacer
                    Spacer(Modifier.width(12.dp))

                    // Header title
                    Text(
                        text = if (workoutId != null) "Edit Workout" else "Add Workout",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { padding ->

        // Content column
        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
        ) {

            // Screen title
            Text(
                text = if (workoutId != null) "Edit Workout" else "Add New Workout",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(16.dp))

            // Workout name
            OutlinedTextField(
                value = workoutName,
                onValueChange = { workoutName = it },
                label = { Text("Workout Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Input fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sets input
                OutlinedTextField(
                    value = sets.toString(),
                    onValueChange = { sets = it.toIntOrNull() ?: 1 },
                    label = { Text("Sets") },
                    modifier = Modifier.weight(1f)
                )
                // Reps input
                OutlinedTextField(
                    value = reps.toString(),
                    onValueChange = { reps = it.toIntOrNull() ?: 1 },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f)
                )
                // Minutes input
                OutlinedTextField(
                    value = minutes.toString(),
                    onValueChange = { minutes = it.toIntOrNull() ?: 0 },
                    label = { Text("Minutes") },
                    modifier = Modifier.weight(1f)
                )
                // Seconds input
                OutlinedTextField(
                    value = seconds.toString(),
                    onValueChange = { seconds = it.toIntOrNull()?.coerceIn(0, 59) ?: 0 },
                    label = { Text("Seconds") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    val exerciseList = listOf(
                        Exercise(
                            name = workoutName,
                            sets = sets,
                            reps = reps,
                            durationSeconds = minutes * 60 + seconds
                        )
                    )
                    coroutineScope.launch {
                        // Update or save
                        if (workoutId != null) {
                            workoutViewModel.updateWorkout(workoutId, exerciseList)
                        } else {
                            workoutViewModel.logWorkout(workoutName, exerciseList)
                        }
                        // Navigate back
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (workoutId != null) "Update Workout" else "Save Workout")
            }
        }
    }
}