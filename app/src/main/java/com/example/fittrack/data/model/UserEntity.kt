package com.example.fittrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// User Profile Table
@Entity(tableName = "user_profile") // table name
data class UserEntity(

    @PrimaryKey
    val uid: String,      // Firebase user UID
    val age: String,
    val height: String,
    val weight: String,
    val goal: String
)