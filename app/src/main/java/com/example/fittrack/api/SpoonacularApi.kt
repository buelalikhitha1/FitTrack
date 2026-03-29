package com.example.fittrack.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

// Food image recognition response
data class FoodRecognitionResponse(
    val id: Int,
    val title: String?,
    val image: String?,
    val nutrition: Nutrition?
)

// Nutrition details container
data class Nutrition(
    val nutrients: List<Nutrient> = emptyList()
)

// Single nutrient information
data class Nutrient(
    val name: String?,
    val amount: Double,
    val unit: String
)

// Food search API response
data class FoodSearchResponse(
    val results: List<FoodSearchResult> = emptyList()
)

// Individual food search result
data class FoodSearchResult(
    val id: Int,
    val title: String?,
    val image: String?,
    val nutrition: Nutrition?
)

// Recipe search API response
data class RecipeSearchResponse(
    val results: List<RecipeSearchResult> = emptyList()
)

// Individual recipe search
data class RecipeSearchResult(
    val id: Int,
    val title: String?,
    val image: String?,
    val nutrition: Nutrition?
)

// Spoonacular API endpoints
interface SpoonacularApi {

    @Multipart
    // food image analysis endpoint
    @POST("food/images/analyze")
    suspend fun recognizeFood(
        @Header("x-api-key") apiKey: String,
        @Part image: MultipartBody.Part
    ): Response<FoodRecognitionResponse> // recognition response

    // search food by name
    @GET("food/ingredients/search")
    suspend fun searchFoodByName(
        @Header("x-api-key") apiKey: String,
        @Query("query") name: String,
        @Query("number") number: Int = 1
    ): Response<FoodSearchResponse> // food search response

    // search recipe by name
    @GET("recipes/complexSearch")
    suspend fun searchRecipeByName(
        @Header("x-api-key") apiKey: String,
        @Query("query") name: String,
        @Query("number") number: Int = 1,
        @Query("addRecipeNutrition") addNutrition: Boolean = true
    ): Response<RecipeSearchResponse> // recipe search response

    // get recipe information
    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Header("x-api-key") apiKey: String,
        @Path("id") id: Int,
        @Query("includeNutrition") includeNutrition: Boolean = true
    ): Response<RecipeSearchResult> // recipe detail response
}