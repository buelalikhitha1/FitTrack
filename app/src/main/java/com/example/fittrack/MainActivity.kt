package com.example.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fittrack.notifications.NotificationHelper
import com.example.fittrack.notifications.ReminderScheduler
import com.example.fittrack.ui.theme.FitTrackTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channel
        NotificationHelper.createChannel(this)

        // Test reminder notification
        ReminderScheduler.scheduleTestReminder(this)

        setContent {
            FitTrackTheme {
                // nav host
                AppNavHost(apiKey = BuildConfig.SPOONACULAR_API_KEY)
            }
        }
    }
}