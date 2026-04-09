package com.example.fittrack.data.dao

import androidx.room.*
import com.example.fittrack.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // insert user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // fetch by uid
    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    fun getUserByUid(uid: String): Flow<UserEntity?>

    // update record
    @Update
    suspend fun updateUser(user: UserEntity)
}