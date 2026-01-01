package com.jesan.tsk.utils

/**
 * Constants used throughout the app
 */
object Constants {
    
    // Notification
    const val NOTIFICATION_CHANNEL_ID = "task_reminders"
    const val NOTIFICATION_CHANNEL_NAME = "Task Reminders"
    const val NOTIFICATION_CHANNEL_DESCRIPTION = "Notifications for upcoming tasks"
    
    // WorkManager tags
    const val WORK_TAG_REMINDER = "task_reminder"
    
    // SharedPreferences keys
    const val PREF_DARK_MODE = "dark_mode"
    const val PREF_NOTIFICATIONS_ENABLED = "notifications_enabled"
    
    // Intent extras
    const val EXTRA_TASK_ID = "task_id"
    
    // Default values
    const val DEFAULT_NOTIFICATION_HOUR = 9
    const val DEFAULT_NOTIFICATION_MINUTE = 0
    
    // AI prompts
    const val AI_SYSTEM_PROMPT = """
        You are a helpful AI study assistant for university students. 
        You help them manage their academic tasks, prioritize their work, and provide study tips.
        Be encouraging, practical, and concise in your responses.
    """.trimIndent()
    
    // Date formats
    const val DATE_DISPLAY_FORMAT = "dd-MM-yyyy"
    const val TIME_DISPLAY_FORMAT = "HH:mm"
}
