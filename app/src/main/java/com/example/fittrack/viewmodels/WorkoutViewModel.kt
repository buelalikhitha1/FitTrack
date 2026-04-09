package com.example.fittrack.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.dao.WorkoutDao
import com.example.fittrack.data.model.Exercise
import com.example.fittrack.data.model.WorkoutEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UI State
data class WorkoutUiState(
    val workouts: List<WorkoutEntity> = emptyList(),
    val loading: Boolean = false
)

// ViewModel
class WorkoutViewModel(
    private val workoutDao: WorkoutDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState

    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Load all workouts
    fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            val data = workoutDao.getWorkoutsForUser(uid)
            _uiState.value = WorkoutUiState(workouts = data, loading = false)
        }
    }

    // Log new workout
    fun logWorkout(name: String, exercises: List<Exercise>) {
        viewModelScope.launch {
            val totalSeconds = exercises.sumOf { it.durationSeconds }
            val workout = WorkoutEntity(
                uid = uid,
                workoutName = name,
                exercises = exercises,
                totalDuration = totalSeconds,
                completedDuration = 0,
                caloriesBurned = 0,
                isCompleted = false
            )
            workoutDao.insertWorkout(workout)
            loadWorkouts()
        }
    }

    // Update existing workout
    fun updateWorkout(workoutId: Int, exercises: List<Exercise>) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(workoutId)
            workout?.let {
                val totalSeconds = exercises.sumOf { it.durationSeconds }
                val updatedWorkout = it.copy(
                    exercises = exercises,
                    totalDuration = totalSeconds
                )
                workoutDao.updateWorkout(updatedWorkout)
                loadWorkouts()
            }
        }
    }

    // Update workout progress
    fun updateWorkoutProgress(workoutId: Int, elapsedSeconds: Int) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(workoutId)
            workout?.let {
                val newCompleted = elapsedSeconds.coerceAtMost(it.totalDuration)
                val caloriesBurned = calculateCalories(it, newCompleted)
                workoutDao.updateWorkoutProgress(workoutId, newCompleted, caloriesBurned)
                loadWorkouts()
            }
        }
    }

    // Mark workout as fully complete
    fun markWorkoutComplete(workoutId: Int) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(workoutId)
            workout?.let {
                val totalCalories = calculateCaloriesForExercises(it.exercises)
                workoutDao.markWorkoutComplete(workoutId, totalCalories)
                loadWorkouts()
            }
        }
    }

    // Calculate calories
    private fun calculateCalories(workout: WorkoutEntity, completedSeconds: Int): Int {
        if (workout.exercises.isEmpty() || workout.totalDuration == 0) return 0
        val ratio = completedSeconds.toFloat() / workout.totalDuration
        return (calculateCaloriesForExercises(workout.exercises) * ratio).toInt()
    }

    // Calculate calories for a list of exercises
    private fun calculateCaloriesForExercises(exercises: List<Exercise>): Int {
        return exercises.sumOf { it.durationSeconds * it.caloriesPerMinute / 60 }
    }

    // Overall completion progress
    fun getCompletionProgress(): Float {
        val workouts = _uiState.value.workouts
        if (workouts.isEmpty()) return 0f
        val completedCount = workouts.count { it.isCompleted }
        return completedCount.toFloat() / workouts.size
    }

    // Delete a workout
    fun deleteWorkout(workoutId: Int) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(workoutId)
            workout?.let {
                workoutDao.deleteWorkout(it)
                loadWorkouts()
            }
        }
    }

    // Get a single workout as StateFlow
    fun getWorkout(workoutId: Int) = MutableStateFlow<WorkoutEntity?>(null).apply {
        viewModelScope.launch {
            value = workoutDao.getWorkoutById(workoutId)
        }
    }
}

// ViewModel Factory
class WorkoutViewModelFactory(
    private val workoutDao: WorkoutDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(workoutDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}