package com.example.fittrack.data.dao

import androidx.room.*
import com.example.fittrack.data.model.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    // insert food
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity)

    // recent foods
    @Query("""
        SELECT * FROM food_log 
        WHERE uid = :uid
        ORDER BY id DESC 
        LIMIT 20
    """)
    fun getRecentFoodLogs(uid: String): Flow<List<FoodEntity>>

    // all foods
    @Query("""
        SELECT * FROM food_log 
        WHERE uid = :uid
        ORDER BY id DESC
    """)
    fun getAllFoodLogs(uid: String): Flow<List<FoodEntity>>

    // today's total calories
    @Query("""
        SELECT SUM(calories) 
        FROM food_log 
        WHERE uid = :uid AND date = :date
    """)
    fun getTodayCalories(uid: String, date: String): Flow<Double?>

    // delete food
    @Delete
    suspend fun deleteFood(food: FoodEntity)

    // clear all foods
    @Query("DELETE FROM food_log WHERE uid = :uid")
    suspend fun clearFoodLogs(uid: String)
}