package com.example.fittrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.components.BottomNavBar
import com.example.fittrack.viewmodels.WorkoutViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    workoutId: Int,
    workoutViewModel: WorkoutViewModel
) {

    // UI state
    val uiState by workoutViewModel.uiState.collectAsState()
    val workout = uiState.workouts.firstOrNull { it.id == workoutId }

    // Timer states
    var isPlaying by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Load duration
    LaunchedEffect(workout) {
        workout?.let { elapsedTime = it.completedDuration }
    }

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
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // Header title
                    Text(
                        text = "Workout Details",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { padding ->

        // Scroll state
        val scrollState = rememberScrollState()

        // Main column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Workout content
            workout?.let { w ->
                val exercise = w.exercises.firstOrNull()

                // Time calculations
                val totalMinutes = w.totalDuration / 60
                val totalSeconds = w.totalDuration % 60
                val elapsedMinutes = elapsedTime / 60
                val elapsedSeconds = elapsedTime % 60

                // Calories calculation
                val dynamicCalories = if (w.totalDuration > 0) {
                    (w.exercises.sumOf { it.durationSeconds * it.caloriesPerMinute / 60 } *
                            elapsedTime / w.totalDuration).toInt()
                } else 0

                // Workout info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    Column(Modifier.padding(20.dp)) {

                        // Workout name
                        Text(
                            w.workoutName,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(Modifier.height(13.dp))

                        // Exercise details
                        exercise?.let { ex ->

                            // Sets text
                            Text(
                                "Sets: ${ex.sets}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(Modifier.height(8.dp))

                            // Reps text
                            Text(
                                "Reps: ${ex.reps}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(Modifier.height(8.dp))

                            // Duration text
                            Text(
                                "Duration: ${ex.durationSeconds / 60}:" +
                                        "${(ex.durationSeconds % 60).toString().padStart(2, '0')} min",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Progress card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Progress title
                        Text(
                            "Workout Progress",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(14.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = elapsedTime / w.totalDuration.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                        )

                        Spacer(Modifier.height(14.dp))

                        // Time display
                        Text(
                            "${elapsedMinutes}:${elapsedSeconds.toString().padStart(2, '0')} / " +
                                    "${totalMinutes}:${totalSeconds.toString().padStart(2, '0')} min",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(8.dp))

                        // Calories text
                        Text(
                            "Calories Burned: $dynamicCalories kcal",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Control buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Start timer button
                    Button(
                        onClick = {
                            if (!isPlaying) {
                                isPlaying = true
                                coroutineScope.launch {

                                    // Timer loop
                                    while (elapsedTime < w.totalDuration && isPlaying) {
                                        delay(1000)
                                        elapsedTime++
                                        workoutViewModel.updateWorkoutProgress(w.id, elapsedTime)
                                    }

                                    // Mark complete
                                    if (elapsedTime >= w.totalDuration) {
                                        workoutViewModel.markWorkoutComplete(w.id)
                                        isPlaying = false
                                    }
                                }
                            } else {
                                isPlaying = false
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {

                        // Play icon
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null
                        )

                        Spacer(Modifier.width(8.dp))

                        // Button text
                        Text(
                            when {
                                isPlaying -> "Pause"
                                elapsedTime >= w.totalDuration -> "Completed"
                                else -> "Start / Resume"
                            }
                        )
                    }

                    // Complete button
                    if (!w.isCompleted) {
                        Button(
                            onClick = {

                                // Complete workout
                                elapsedTime = w.totalDuration
                                workoutViewModel.markWorkoutComplete(w.id)
                                isPlaying = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        ) {

                            // Check icon
                            Icon(Icons.Default.Check, contentDescription = null)

                            Spacer(Modifier.width(8.dp))

                            // Complete text
                            Text("Complete Now")
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Status text
                Text(
                    if (elapsedTime < w.totalDuration)
                        "Elapsed Time: ${elapsedMinutes}:${elapsedSeconds.toString().padStart(2, '0')}"
                    else "Workout Completed!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (elapsedTime >= w.totalDuration)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onBackground
                )

            } ?:

            // Error text
            Text(
                "Workout not found",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}