package com.example.fittrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room database table for food logs
@Entity(tableName = "food_log")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val imageUrl: String = "",
    val foodType: String = "Unknown"
)