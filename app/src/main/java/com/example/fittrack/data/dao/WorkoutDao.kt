package com.example.fittrack.data.dao

import androidx.room.*
import com.example.fittrack.data.model.WorkoutEntity

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workout_log ORDER BY date DESC")
    suspend fun getAllWorkouts(): List<WorkoutEntity>

    @Query("SELECT * FROM workout_log ORDER BY date DESC LIMIT 10")
    suspend fun getRecentWorkouts(): List<WorkoutEntity>
}