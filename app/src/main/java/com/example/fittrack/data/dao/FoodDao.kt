package com.example.fittrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittrack.data.model.FoodEntity

// Room database access object
@Dao
interface FoodDao {

    // insert or replace food
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity)

    // get latest 20 foods
    @Query("SELECT * FROM food_log ORDER BY id DESC LIMIT 20")
    suspend fun getRecentFoodLogs(): List<FoodEntity>
}