package com.jesan.tsk.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jesan.tsk.data.repository.TaskRepository
import com.jesan.tsk.utils.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker for handling task reminder notifications
 */
@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val taskId = inputData.getString(Constants.EXTRA_TASK_ID) ?: return Result.failure()
            val isDueToday = inputData.getBoolean("is_due_today", false)
            
            // Get task from database
            val task = taskRepository.getTaskById(taskId)
            
            if (task != null && !task.isCompleted) {
                // Show notification
                notificationHelper.showTaskReminder(
                    taskId = task.id,
                    taskTitle = task.title,
                    courseName = task.courseName,
                    isDueToday = isDueToday
                )
                Result.success()
            } else {
                // Task doesn't exist or is completed
                Result.success()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
