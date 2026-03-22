package com.example.fittrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Table name in DB
@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,      // Firebase user UID
    val age: String,
    val height: String,
    val weight: String,
    val goal: String
)