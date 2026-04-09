package com.example.fittrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Food Log Table
@Entity(tableName = "food_log") // table name
data class FoodEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: String,
    val name: String,
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val imageUrl: String = "",
    val foodType: String = "Unknown",
    val time: String,
    val date: String
)