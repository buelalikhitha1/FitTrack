package com.example.fittrack.data.dao

import androidx.room.*
import com.example.fittrack.data.model.WorkoutEntity

@Dao
interface WorkoutDao {

    // insert workout
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    // update record
    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    // fetch by user
    @Query("SELECT * FROM workouts WHERE uid = :uid ORDER BY date DESC")
    suspend fun getWorkoutsForUser(uid: String): List<WorkoutEntity>

    // fetch by id
    @Query("SELECT * FROM workouts WHERE id = :workoutId LIMIT 1")
    suspend fun getWorkoutById(workoutId: Int): WorkoutEntity?

    // delete record
    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    // Update workout progress
    @Query("""
        UPDATE workouts
        SET completedDuration = :completedDuration,
            caloriesBurned = :caloriesBurned
        WHERE id = :workoutId
    """)
    suspend fun updateWorkoutProgress(
        workoutId: Int,
        completedDuration: Int,
        caloriesBurned: Int
    )

    // Mark workout as fully complete
    @Query("""
        UPDATE workouts
        SET completedDuration = totalDuration,
            caloriesBurned = :caloriesBurned,
            isCompleted = 1
        WHERE id = :workoutId
    """)
    suspend fun markWorkoutComplete(
        workoutId: Int,
        caloriesBurned: Int
    )
}