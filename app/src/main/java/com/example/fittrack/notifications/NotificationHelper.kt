package com.example.fittrack.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.fittrack.R

// Notification Helper
object NotificationHelper {

    // channel id
    private const val CHANNEL_ID = "fittrack_reminder"

    fun createChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // channel name
            val channel = NotificationChannel(
                CHANNEL_ID,
                "FitTrack Reminders",
                NotificationManager.IMPORTANCE_DEFAULT // default importance
            ).apply {
                // channel description
                description = "Daily reminders to log meals and workouts"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            manager.createNotificationChannel(channel) // create channel
        }
    }

    // show notification function
    fun showNotification(context: Context, title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // notification icon
            .setContentTitle(title)  // notification title
            .setContentText(message) // notification text
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification) // send
    }
}