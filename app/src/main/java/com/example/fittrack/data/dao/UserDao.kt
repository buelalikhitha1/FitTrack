package com.example.fittrack.data.dao

import androidx.room.*
import com.example.fittrack.data.model.UserEntity

@Dao
interface UserDao {

    // Add user to database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Get user by UID
    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    suspend fun getUserByUid(uid: String): UserEntity?
}