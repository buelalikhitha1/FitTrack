package com.example.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fittrack.ui.theme.FitTrackTheme

// Main Activity
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitTrackTheme {
                // API key
                AppNavHost(apiKey = BuildConfig.SPOONACULAR_API_KEY)
            }
        }
    }
}