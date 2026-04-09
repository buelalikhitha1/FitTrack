package com.example.fittrack.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fittrack.api.SpoonacularApi
import com.example.fittrack.data.dao.FoodDao
import com.example.fittrack.data.model.FoodEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FoodViewModel(
    private val foodDao: FoodDao,
    private val api: SpoonacularApi
) : ViewModel() {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // loading
    val loading = mutableStateOf(false)

    // error
    val errorMessage = mutableStateOf<String?>(null)

    // recognized result
    val recognizedFood = mutableStateOf<FoodEntity?>(null)

    // recent foods
    val recentFoods = mutableStateOf<List<FoodEntity>>(emptyList())

    // all foods
    val allFoods = mutableStateOf<List<FoodEntity>>(emptyList())

    // today calories
    val todayCalories = mutableStateOf(0.0)

    // observe DB
    init {
        observeFoodLogs()
    }

    // Observe Food Logs
    private fun observeFoodLogs() {

        viewModelScope.launch {

            foodDao.getRecentFoodLogs(uid)
                .collectLatest {
                    recentFoods.value = it // update recent
                }
        }

        viewModelScope.launch {
            foodDao.getAllFoodLogs(uid)
                .collectLatest {
                    allFoods.value = it // update all
                }
        }

        viewModelScope.launch {

            foodDao.getTodayCalories(uid, getTodayDate())
                .collectLatest {
                    todayCalories.value = it ?: 0.0 // today calories
                }
        }
    }

    // Analyze Food
    fun recognizeFood(
        file: File?,
        apiKey: String,
        foodName: String
    ) {

        loading.value = true
        errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {

            try {
                var foodEntity: FoodEntity? = null

                // Recipe Search
                if (foodName.isNotBlank()) {
                    val searchResp =
                        api.searchRecipeByName(apiKey, foodName)
                    if (searchResp.isSuccessful) {
                        val recipe =
                            searchResp.body()?.results?.firstOrNull()
                        recipe?.let {
                            val detailResp =
                                api.getRecipeInformation(apiKey, it.id)
                            if (detailResp.isSuccessful) {
                                val detail = detailResp.body()
                                val nutrients =
                                    detail?.nutrition?.nutrients
                                        ?.associate { n ->
                                            val key = n.name?.lowercase() ?: ""
                                            key to n.amount
                                        } ?: emptyMap()

                                foodEntity = FoodEntity(
                                    uid = uid,
                                    name = foodName,
                                    calories = nutrients["calories"] ?: 0.0,
                                    protein = nutrients["protein"] ?: 0.0,
                                    carbs = nutrients["carbohydrates"] ?: 0.0,
                                    fat = nutrients["fat"] ?: 0.0,
                                    imageUrl = detail?.image ?: "",
                                    foodType = "Dish",
                                    time = getCurrentTime(),
                                    date = getTodayDate()
                                )
                            }
                        }
                    }
                }

                //  Ingredient Search
                if (foodEntity == null && foodName.isNotBlank()) {
                    val ingResp =
                        api.searchFoodByName(apiKey, foodName)

                    if (ingResp.isSuccessful) {
                        val ing =
                            ingResp.body()?.results
                                ?.firstOrNull {
                                    it.nutrition?.nutrients?.isNotEmpty() == true
                                }
                        ing?.let {
                            val nutrients =
                                it.nutrition?.nutrients
                                    ?.associate { n ->
                                        val key = n.name?.lowercase() ?: ""
                                        key to n.amount
                                    } ?: emptyMap()

                            foodEntity = FoodEntity(
                                uid = uid,
                                name = foodName,
                                calories = nutrients["calories"] ?: 0.0,
                                protein = nutrients["protein"] ?: 0.0,
                                carbs = nutrients["carbohydrates"] ?: 0.0,
                                fat = nutrients["fat"] ?: 0.0,
                                imageUrl = it.image ?: "",
                                foodType = "Ingredient",
                                time = getCurrentTime(),
                                date = getTodayDate()
                            )
                        }
                    }
                }

                //  Image Recognition
                if (foodEntity == null && file != null && file.exists()) {
                    val reqFile =
                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val part =
                        MultipartBody.Part.createFormData(
                            "file",
                            file.name,
                            reqFile
                        )
                    val imgResp =
                        api.recognizeFood(apiKey, part)
                    if (imgResp.isSuccessful) {
                        val body = imgResp.body()
                        val nutrients =
                            body?.nutrition?.nutrients
                                ?.associate { n ->
                                    val key = n.name?.lowercase() ?: ""
                                    key to n.amount
                                } ?: emptyMap()

                        foodEntity = FoodEntity(
                            uid = uid,
                            name = foodName,
                            calories = nutrients["calories"] ?: 0.0,
                            protein = nutrients["protein"] ?: 0.0,
                            carbs = nutrients["carbohydrates"] ?: 0.0,
                            fat = nutrients["fat"] ?: 0.0,
                            imageUrl = body?.image ?: "",
                            foodType = "Image",
                            time = getCurrentTime(),
                            date = getTodayDate()
                        )
                    }
                }

                // Save to RoomDB
                foodEntity?.let {
                    recognizedFood.value = it
                    foodDao.insertFood(it)
                } ?: run {
                    errorMessage.value =
                        "Could not retrieve nutrition for \"$foodName\""
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.message}"
            } finally {
                loading.value = false
            }
        }
    }

    //  Helpers Functions
    private fun getCurrentTime(): String {
        val format =
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(Date())
    }
    private fun getTodayDate(): String {
        val format =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date())
    }

    // delete food log
    fun deleteFood(foodId: Int) {
        viewModelScope.launch {
            // Find the FoodEntity by ID in the current list
            val foodToDelete = allFoods.value.find { it.id == foodId }
            if (foodToDelete != null) {
                foodDao.deleteFood(foodToDelete) // Call DAO
            }
        }
    }
}

// Food ViewModel Factory
class FoodViewModelFactory(
    private val foodDao: FoodDao,
    private val api: SpoonacularApi
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(foodDao, api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}