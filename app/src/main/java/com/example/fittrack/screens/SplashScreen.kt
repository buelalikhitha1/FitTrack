package com.example.fittrack.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.R
import com.example.fittrack.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance() // Firebase instance
    val currentUser = auth.currentUser // Get current user

    LaunchedEffect(Unit) {
        delay(2000) // Wait 2 seconds
        if (currentUser != null) {
            navController.navigate(Screen.Landing.route) { // Navigate landing
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) { // Navigate login
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    // Fullscreen white
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // App logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "FitTrack Logo",
            modifier = Modifier.size(250.dp)
        )
    }
}