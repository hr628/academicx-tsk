package com.jesan.tsk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jesan.tsk.domain.model.Task
import com.jesan.tsk.domain.model.TaskType

/**
 * Room entity for storing tasks in local database
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val courseName: String,
    val taskType: String, // Stored as string name of enum
    val customTaskType: String?,
    val dueDate: Long, // Timestamp in milliseconds
    val dueTime: String, // Format: "HH:mm"
    val notes: String?,
    val isCompleted: Boolean,
    val createdAt: Long,
    val userId: String?
) {
    /**
     * Convert entity to domain model
     */
    fun toTask(): Task {
        return Task(
            id = id,
            title = title,
            courseName = courseName,
            taskType = try {
                TaskType.valueOf(taskType)
            } catch (e: Exception) {
                TaskType.CUSTOM
            },
            customTaskType = customTaskType,
            dueDate = dueDate,
            dueTime = dueTime,
            notes = notes,
            isCompleted = isCompleted,
            createdAt = createdAt,
            userId = userId
        )
    }
    
    companion object {
        /**
         * Convert domain model to entity
         */
        fun fromTask(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                courseName = task.courseName,
                taskType = task.taskType.name,
                customTaskType = task.customTaskType,
                dueDate = task.dueDate,
                dueTime = task.dueTime,
                notes = task.notes,
                isCompleted = task.isCompleted,
                createdAt = task.createdAt,
                userId = task.userId
            )
        }
    }
}
