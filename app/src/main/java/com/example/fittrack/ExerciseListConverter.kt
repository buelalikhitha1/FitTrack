package com.example.fittrack

import androidx.room.TypeConverter
import com.example.fittrack.data.model.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ExerciseListConverter {

    // Gson instance
    private val gson = Gson()

    @TypeConverter
    fun fromExerciseList(exercises: List<Exercise>?): String {
        return gson.toJson(exercises) // convert list to JSON
    }

    @TypeConverter
    fun toExerciseList(data: String?): List<Exercise> {
        if (data.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<Exercise>>() {}.type
        return gson.fromJson(data, listType) // convert JSON to list
    }
}