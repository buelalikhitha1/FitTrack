package com.example.fittrack.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // API endpoint URL
    private const val BASE_URL = "https://api.spoonacular.com/"

    // HTTP client with timeouts
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Retrofit API instance
    val api: SpoonacularApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            // JSON converter
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            // create API service
            .create(SpoonacularApi::class.java)
    }
}