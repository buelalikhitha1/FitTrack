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
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Navigate to Landing Screen after 2 seconds
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(Screen.Landing.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Background Color
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // FitTrack App Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "FitTrack Logo",
                modifier = Modifier.size(300.dp)
            )
        }
    }
}