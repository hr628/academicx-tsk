package com.jesan.tsk.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility object for date-related operations
 */
object DateUtils {
    
    private const val DATE_FORMAT = "dd-MM-yyyy"
    private const val TIME_FORMAT = "HH:mm"
    private const val DATETIME_FORMAT = "dd-MM-yyyy HH:mm"
    
    /**
     * Format timestamp to date string (dd-MM-yyyy)
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to time string (HH:mm)
     */
    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to datetime string
     */
    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Parse date string to timestamp
     */
    fun parseDate(dateString: String): Long {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            sdf.parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    
    /**
     * Get current timestamp
     */
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
    
    /**
     * Check if date is today
     */
    fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }
    
    /**
     * Check if date is tomorrow
     */
    fun isTomorrow(timestamp: Long): Boolean {
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return tomorrow.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                tomorrow.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }
    
    /**
     * Check if date is within this week
     */
    fun isThisWeek(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        // Check if it's the same week
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                today.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR)
    }
    
    /**
     * Get days until date
     */
    fun getDaysUntil(timestamp: Long): Long {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val targetDate = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val diff = targetDate.timeInMillis - today.timeInMillis
        return diff / (1000 * 60 * 60 * 24)
    }
    
    /**
     * Get urgency label for a task
     */
    fun getUrgencyLabel(timestamp: Long): String? {
        return when {
            isToday(timestamp) -> "Today"
            isTomorrow(timestamp) -> "Tomorrow"
            isThisWeek(timestamp) -> "This week"
            else -> null
        }
    }
    
    /**
     * Get timestamp for a specific time on a date
     */
    fun getTimestampForDateTime(dateTimestamp: Long, timeString: String): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateTimestamp
        }
        
        val timeParts = timeString.split(":")
        if (timeParts.size == 2) {
            calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toIntOrNull() ?: 0)
            calendar.set(Calendar.MINUTE, timeParts[1].toIntOrNull() ?: 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }
        
        return calendar.timeInMillis
    }
    
    /**
     * Get timestamp for 1 day before a date at 9:00 AM
     */
    fun getOneDayBeforeAt9AM(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    /**
     * Get timestamp for same day at 9:00 AM
     */
    fun getSameDayAt9AM(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
