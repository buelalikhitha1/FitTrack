package com.example.fittrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// User Profile Table
@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,      // Firebase user UID
    val age: String,
    val height: String,
    val weight: String,
    val goal: String
)