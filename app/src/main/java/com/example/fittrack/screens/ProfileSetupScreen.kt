package com.example.fittrack.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fittrack.AuthViewModel
import com.example.fittrack.Screen

@Composable
fun ProfileSetupScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    // State variables for user input
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Profile Setup",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))

        // Input Fields
        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))

        // Height
        TextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (cm)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))

        // Weight
        TextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))

        // Fitness Goal
        TextField(
            value = goal,
            onValueChange = { goal = it },
            label = { Text("Fitness Goal") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(24.dp))

        // Save Profile Button
        Button(
            onClick = {
                if (age.isBlank() || height.isBlank() || weight.isBlank() || goal.isBlank()) {
                    Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                authViewModel.saveUserProfile(age, height, weight, goal) {
                    Toast.makeText(context, "Profile saved successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Profile", fontSize = 18.sp)
        }

        Spacer(Modifier.height(16.dp))

        // Circle Loader
        if (authViewModel.loading.value) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        // Error Message
        authViewModel.errorMessage.value?.let { msg ->
            Spacer(Modifier.height(12.dp))
            Text(msg, color = MaterialTheme.colorScheme.error)
        }
    }
}