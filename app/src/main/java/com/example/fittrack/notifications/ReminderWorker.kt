package com.example.fittrack.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

// Notifications Reminder Worker
class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        // Show daily reminder notification
        NotificationHelper.showNotification(
            applicationContext,
            "FitTrack Reminder", // title
            "Don't forget to log your meals and workouts today!" // message
        )
        return Result.success()
    }
}

object ReminderScheduler {

    // work name
    private const val DAILY_WORK_NAME = "fittrack_daily_reminder"

    // Schedule Daily Reminders
    fun scheduleDailyReminder(context: Context) {

        // daily interval
        val dailyWork = PeriodicWorkRequestBuilder<ReminderWorker>(
            1, TimeUnit.DAYS
        ).build()

        // update existing
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWork
        )
    }

    // Cancel Daily Reminders
    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_WORK_NAME)
    }

    // Test Reminder
    fun scheduleTestReminder(context: Context) {
        val testWork = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES) // test delay
            .build()

        WorkManager.getInstance(context).enqueue(testWork)
    }
}