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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fittrack.viewmodels.FoodViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun LogFoodScreen(
    navController: NavController,
    foodViewModel: FoodViewModel,
    apiKey: String
) {

    // get current context
    val context = LocalContext.current
    // vertical scroll state
    val scrollState = rememberScrollState()

    // selected image URI
    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    // image file object
    var imageFile by remember { mutableStateOf<File?>(null) }
    var foodName by remember { mutableStateOf("") } // food name input

    // gallery image picker
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImage = it
            imageFile = uriToFile(it, context)
        }
    }

    // camera preview launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val file = bitmapToFile(it, context) // convert bitmap to file
            selectedImage = Uri.fromFile(file)
            imageFile = file // store image file
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(8.dp))

        // log food screen title
        Text(
            text = "Log Food",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // capture
        Text(
            text = "Capture or upload food and track nutrition",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Image selection buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Gallery")
            }

            Button(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
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
                        contentDescription = "Selected Food Image",
                        modifier = Modifier.size(220.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // Food Name input
        OutlinedTextField(
            value = foodName,
            onValueChange = { foodName = it },
            label = { Text("Food Name") },
            leadingIcon = {
                Icon(Icons.Default.Fastfood, contentDescription = null)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(Modifier.height(18.dp))

        // Analyze Button
        Button(
            onClick = {
                if (foodName.isNotBlank())
                    foodViewModel.recognizeFood(imageFile, apiKey, foodName)
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

        // circle loader
        if (foodViewModel.loading.value) {
            CircularProgressIndicator()
            Spacer(Modifier.height(10.dp))
        }

        // show error message
        foodViewModel.errorMessage.value?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Recognized food result
        foodViewModel.recognizedFood.value?.let { food ->

            Spacer(Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp
            ) {

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    // recognized food name
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Divider()

                    Text("Calories: ${food.calories.toInt()} kcal")
                    Text("Protein: ${"%.1f".format(food.protein)} g")
                    Text("Carbs: ${"%.1f".format(food.carbs)} g")
                    Text("Fat: ${"%.1f".format(food.fat)} g")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // recent logs title
        Text(
            text = "Recent Logs",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(10.dp))

        foodViewModel.recentFoods.value.forEach { food ->

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                tonalElevation = 3.dp
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    // logged food name
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(4.dp))

                    Text("Calories: ${food.calories.toInt()} kcal")
                    Text("Protein: ${"%.1f".format(food.protein)} g")
                    Text("Carbs: ${"%.1f".format(food.carbs)} g")
                    Text("Fat: ${"%.1f".format(food.fat)} g")
                }
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}

// Convert URI to file
fun uriToFile(uri: Uri, context: android.content.Context): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image.jpg") // create temp file
        inputStream?.use { input -> FileOutputStream(file).use { output -> input.copyTo(output) } }
        file
    } catch (e: Exception) { e.printStackTrace(); null }
}

// Convert bitmap to file
fun bitmapToFile(bitmap: Bitmap, context: android.content.Context): File {
    val file = File(context.cacheDir, "captured_image.jpg") // create captured file
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
    return file
}