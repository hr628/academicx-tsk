package com.jesan.tsk.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jesan.tsk.MainActivity
import com.jesan.tsk.R
import com.jesan.tsk.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for creating and managing notifications
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val notificationManager = 
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    /**
     * Create notification channel (required for Android O+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = Constants.NOTIFICATION_CHANNEL_DESCRIPTION
                enableVibration(true)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Show task reminder notification
     */
    fun showTaskReminder(
        taskId: String,
        taskTitle: String,
        courseName: String,
        isDueToday: Boolean
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constants.EXTRA_TASK_ID, taskId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = if (isDueToday) {
            context.getString(R.string.notification_title_today)
        } else {
            context.getString(R.string.notification_title_tomorrow)
        }
        
        val body = context.getString(R.string.notification_body, taskTitle, courseName)
        
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(taskId.hashCode(), notification)
    }
    
    /**
     * Cancel notification for a task
     */
    fun cancelNotification(taskId: String) {
        notificationManager.cancel(taskId.hashCode())
    }
}
