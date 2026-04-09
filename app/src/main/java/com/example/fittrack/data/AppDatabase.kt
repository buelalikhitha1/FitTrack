package com.example.fittrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fittrack.data.dao.FoodDao
import com.example.fittrack.data.dao.UserDao
import com.example.fittrack.data.dao.WorkoutDao
import com.example.fittrack.data.model.FoodEntity
import com.example.fittrack.data.model.UserEntity
import com.example.fittrack.data.model.WorkoutEntity

// Room database
@Database(entities = [UserEntity::class, FoodEntity::class, WorkoutEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // access user DAO
    abstract fun userDao(): UserDao
    // access food DAO
    abstract fun foodDao(): FoodDao
    // access workout DAO
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fittrack_db" // database name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}