package com.example.todoapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val taskTitle = inputData.getString("taskTitle") ?: return Result.failure()

        // Step 1: Check if notification permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, skip showing the notification
            return Result.failure()
        }

        // Step 2: Create a notification
        val notification = NotificationCompat.Builder(applicationContext, "task_reminder_channel")
            .setContentTitle("Task Reminder")
            .setContentText("Don't forget: $taskTitle")
            .setSmallIcon(R.drawable.ic_notifications)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Step 3: Show the notification
        NotificationManagerCompat.from(applicationContext).notify(taskTitle.hashCode(), notification)

        return Result.success()
    }
}