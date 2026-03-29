package com.example.fittrack.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fittrack.api.SpoonacularApi
import com.example.fittrack.data.dao.FoodDao
import com.example.fittrack.data.model.FoodEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class FoodViewModel(private val foodDao: FoodDao, private val api: SpoonacularApi) : ViewModel() {

    // loading state
    val loading = mutableStateOf(false)

    // error message
    val errorMessage = mutableStateOf<String?>(null)

    // recognized food
    val recognizedFood = mutableStateOf<FoodEntity?>(null)

    // recent foods list
    val recentFoods = mutableStateOf<List<FoodEntity>>(emptyList())

    fun recognizeFood(file: File?, apiKey: String, foodName: String) {
        loading.value = true
        errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                var foodEntity: FoodEntity? = null // result food entity

                // Recipe search
                if (foodName.isNotBlank()) { // check food name valid
                    val searchResp = api.searchRecipeByName(apiKey, foodName)
                    if (searchResp.isSuccessful) {
                        val recipe = searchResp.body()?.results?.firstOrNull()
                        recipe?.let {
                            val detailResp = api.getRecipeInformation(apiKey, it.id)
                            if (detailResp.isSuccessful) {
                                val detail = detailResp.body()
                                val nutrients = detail?.nutrition?.nutrients
                                    ?.associate { n ->
                                        val key = n.name?.lowercase() ?: ""
                                        key to n.amount
                                    } ?: emptyMap()
                                foodEntity = FoodEntity(
                                    name = foodName,
                                    calories = nutrients["calories"] ?: 0.0,
                                    protein = nutrients["protein"] ?: 0.0,
                                    carbs = nutrients["carbohydrates"] ?: 0.0,
                                    fat = nutrients["fat"] ?: 0.0,
                                    imageUrl = detail?.image ?: "",
                                    foodType = "Dish"
                                )
                            }
                        }
                    }
                }

                // Ingredient search
                if (foodEntity == null && foodName.isNotBlank()) {
                    val ingResp = api.searchFoodByName(apiKey, foodName) // search ingredient API
                    if (ingResp.isSuccessful) {
                        val ing = ingResp.body()?.results
                            ?.firstOrNull { it.nutrition?.nutrients?.isNotEmpty() == true }
                        ing?.let {
                            val nutrients = it.nutrition?.nutrients
                                ?.associate { n ->
                                    val key = n.name?.lowercase() ?: ""
                                    key to n.amount
                                } ?: emptyMap()
                            foodEntity = FoodEntity(
                                name = foodName,
                                calories = nutrients["calories"] ?: 0.0,
                                protein = nutrients["protein"] ?: 0.0,
                                carbs = nutrients["carbohydrates"] ?: 0.0,
                                fat = nutrients["fat"] ?: 0.0,
                                imageUrl = it.image ?: "",
                                foodType = "Fruit/Vegetable"
                            )
                        }
                    }
                }

                // Image recognition
                if (foodEntity == null && file != null && file.exists()) {
                    val reqFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull()) // create image request
                    val part = MultipartBody.Part.createFormData("file", file.name, reqFile)
                    val imgResp = api.recognizeFood(apiKey, part) // image recognition API
                    if (imgResp.isSuccessful) {
                        val body = imgResp.body()
                        val nutrients = body?.nutrition?.nutrients
                            ?.associate { n ->
                                val key = n.name?.lowercase() ?: ""
                                key to n.amount
                            } ?: emptyMap()
                        foodEntity = FoodEntity(
                            name = foodName,  // user-provided name
                            calories = nutrients["calories"] ?: 0.0,
                            protein = nutrients["protein"] ?: 0.0,
                            carbs = nutrients["carbohydrates"] ?: 0.0,
                            fat = nutrients["fat"] ?: 0.0,
                            imageUrl = body?.image ?: "",
                            foodType = "Unknown"
                        )
                    }
                }

                // Save & Update
                foodEntity?.let {
                    recognizedFood.value = it
                    foodDao.insertFood(it) // save to database
                    loadRecentFoods()
                } ?: run {
                    errorMessage.value = "Could not retrieve nutrition for \"$foodName\"" // show error message
                }

            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.message}" // show error message
            } finally {
                loading.value = false
            }
        }
    }

    // load recent foods
    fun loadRecentFoods() {
        viewModelScope.launch(Dispatchers.IO) {
            recentFoods.value = foodDao.getRecentFoodLogs()
        }
    }
}

// ViewModel factory provider
class FoodViewModelFactory(
    private val foodDao: FoodDao,
    private val api: SpoonacularApi
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) { // check correct viewmodel class
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(foodDao, api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}