package com.example.fittrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_log")
data class WorkoutEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val exercise: String,
    val sets: Int,
    val reps: Int,
    val duration: Int, // minutes
    val date: Long
)