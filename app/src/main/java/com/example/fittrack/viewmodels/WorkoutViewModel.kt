package com.example.fittrack.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.dao.WorkoutDao
import com.example.fittrack.data.model.WorkoutEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    val workouts = mutableStateOf<List<WorkoutEntity>>(emptyList())

    fun addWorkout(
        exercise: String,
        sets: Int,
        reps: Int,
        duration: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val workout = WorkoutEntity(
                exercise = exercise,
                sets = sets,
                reps = reps,
                duration = duration,
                date = System.currentTimeMillis()
            )

            workoutDao.insertWorkout(workout)
            loadWorkouts()
        }
    }

    fun loadWorkouts() {
        viewModelScope.launch(Dispatchers.IO) {
            workouts.value = workoutDao.getAllWorkouts()
        }
    }
}