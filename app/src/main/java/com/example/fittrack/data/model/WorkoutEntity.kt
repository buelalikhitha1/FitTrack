package com.example.fittrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.fittrack.ExerciseListConverter

// Workouts Table
@Entity(tableName = "workouts") // table name
@TypeConverters(ExerciseListConverter::class)
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,
    val workoutName: String,
    val exercises: List<Exercise> = emptyList(),
    val totalDuration: Int = exercises.sumOf { it.durationSeconds },
    val completedDuration: Int = 0,
    val caloriesBurned: Int = 0,
    val isCompleted: Boolean = false,
    val date: Long = System.currentTimeMillis() // timestamp
)

// Exercise Class
data class Exercise(
    val name: String,
    val sets: Int,
    val reps: Int,
    val durationSeconds: Int,
    val caloriesPerMinute: Int = 5 // default calories
) {
    val minutes: Int get() = durationSeconds / 60
    val seconds: Int get() = durationSeconds % 60
}