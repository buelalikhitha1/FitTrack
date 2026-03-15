package com.example.fittrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LandingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Main title
            Text(
                text = "Welcome to FitTrack",
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            )
            // Space before Text
            Spacer(modifier = Modifier.height(20.dp))

            // Subtitle text
            Text(
                text = "Track your fitness, calories, and workouts easily!",
                fontSize = 16.sp
            )
            // Space before button
            Spacer(modifier = Modifier.height(40.dp))

            // Get Started Button
            Button(onClick = { }) {
                Text("Get Started")
            }
        }
    }
}