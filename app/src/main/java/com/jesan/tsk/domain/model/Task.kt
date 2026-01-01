package com.jesan.tsk.domain.model

/**
 * Domain model representing a task in the app
 */
data class Task(
    val id: String,
    val title: String,
    val courseName: String,
    val taskType: TaskType,
    val customTaskType: String? = null,
    val dueDate: Long, // Timestamp in milliseconds
    val dueTime: String, // Format: "HH:mm"
    val notes: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String? = null
)
