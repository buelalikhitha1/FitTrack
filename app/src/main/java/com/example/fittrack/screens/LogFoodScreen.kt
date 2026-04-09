package com.example.fittrack.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fittrack.Screen
import com.example.fittrack.components.BottomNavBar
import com.example.fittrack.viewmodels.FoodViewModel
import java.io.File
import java.io.FileOutputStream

// Log food screen
@Composable
fun LogFoodScreen(
    navController: NavController,
    foodViewModel: FoodViewModel,
    apiKey: String
) {

    // Context setup
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Image states
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var foodName by remember { mutableStateOf("") }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImage = it
            imageFile = uriToFile(it, context)
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val file = bitmapToFile(it, context)
            selectedImage = Uri.fromFile(file)
            imageFile = file
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        // Main column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Top header
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Header content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 20.dp)
                ) {
                    Text(
                        text = "Log Food",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Capture or upload food and analyze nutrition",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Scroll content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(18.dp))

                // Image buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Gallery button
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Image, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Gallery")
                    }

                    // Camera button
                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CameraAlt, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Camera")
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Image preview
                selectedImage?.let {

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Box(
                            modifier = Modifier.padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = null,
                                modifier = Modifier.size(220.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                // Food input
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text("Food Name") },
                    leadingIcon = {
                        Icon(Icons.Default.Fastfood, null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(18.dp))

                // Analyze button
                Button(
                    onClick = {
                        foodViewModel.recognizeFood(
                            imageFile,
                            apiKey,
                            foodName
                        )
                    },
                    enabled = foodName.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Analyze Food")
                }

                Spacer(Modifier.height(18.dp))

                // Loading indicator
                if (foodViewModel.loading.value) {
                    CircularProgressIndicator()
                }

                // Error message
                foodViewModel.errorMessage.value?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Result card
                foodViewModel.recognizedFood.value?.let { food ->

                    Spacer(Modifier.height(20.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        tonalElevation = 6.dp,
                        shadowElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        // Result content
                        Column(modifier = Modifier.padding(20.dp)) {

                            // Food title
                            Text(
                                text = food.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            Divider(
                                color = Color.Gray.copy(alpha = 0.4f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            // Nutrition data
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                                Text(
                                    text = "Calories: ${food.calories.toInt()} kcal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Protein: ${food.protein} g",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Carbs: ${food.carbs} g",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Fat: ${food.fat} g",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // History button
                OutlinedButton(
                    onClick = {
                        navController.navigate(Screen.FoodHistory.route)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.History, null)
                    Spacer(Modifier.width(8.dp))
                    Text("View Food History")
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

// URI to file
fun uriToFile(uri: Uri, context: android.content.Context): File? {
    return try {
        // Open stream
        val inputStream = context.contentResolver.openInputStream(uri)
        // Cache file
        val file = File(context.cacheDir, "temp_image.jpg")
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        null
    }
}

// Bitmap to file
fun bitmapToFile(bitmap: Bitmap, context: android.content.Context): File {
    // Cache file
    val file = File(context.cacheDir, "captured.jpg")
    FileOutputStream(file).use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
    return file
}