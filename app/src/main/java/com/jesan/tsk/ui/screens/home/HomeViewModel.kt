package com.jesan.tsk.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jesan.tsk.data.repository.TaskRepository
import com.jesan.tsk.domain.model.Task
import com.jesan.tsk.notifications.TaskReminderWorker
import com.jesan.tsk.utils.Constants
import com.jesan.tsk.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for Home Screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val workManager: WorkManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadTasks()
    }
    
    /**
     * Load all tasks from repository
     */
    private fun loadTasks() {
        viewModelScope.launch {
            try {
                combine(
                    taskRepository.getUpcomingTasks(),
                    taskRepository.getCompletedTasks()
                ) { upcoming, completed ->
                    HomeUiState.Success(
                        upcomingTasks = upcoming,
                        completedTasks = completed
                    )
                }.catch { error ->
                    _uiState.value = HomeUiState.Error(error.message ?: "Unknown error")
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Toggle task completion status
     */
    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                taskRepository.toggleTaskCompletion(taskId, !isCompleted)
                
                // Cancel notifications if task is being marked complete
                if (!isCompleted) {
                    cancelTaskNotifications(taskId)
                } else {
                    // Reschedule notifications if marking incomplete
                    val task = taskRepository.getTaskById(taskId)
                    task?.let { scheduleTaskNotifications(it) }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    /**
     * Delete a task
     */
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTaskById(taskId)
                cancelTaskNotifications(taskId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    /**
     * Schedule notifications for a task
     */
    fun scheduleTaskNotifications(task: Task) {
        // Cancel existing notifications first
        cancelTaskNotifications(task.id)
        
        val now = System.currentTimeMillis()
        
        // Schedule notification 1 day before at 9 AM
        val oneDayBefore = DateUtils.getOneDayBeforeAt9AM(task.dueDate)
        if (oneDayBefore > now) {
            val delay = oneDayBefore - now
            scheduleNotification(task.id, delay, isDueToday = false)
        }
        
        // Schedule notification on due date at 9 AM
        val sameDay = DateUtils.getSameDayAt9AM(task.dueDate)
        if (sameDay > now) {
            val delay = sameDay - now
            scheduleNotification(task.id, delay, isDueToday = true)
        }
    }
    
    /**
     * Schedule a notification work request
     */
    private fun scheduleNotification(taskId: String, delayMillis: Long, isDueToday: Boolean) {
        val data = Data.Builder()
            .putString(Constants.EXTRA_TASK_ID, taskId)
            .putBoolean("is_due_today", isDueToday)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("${Constants.WORK_TAG_REMINDER}_$taskId")
            .build()
        
        workManager.enqueue(workRequest)
    }
    
    /**
     * Cancel notifications for a task
     */
    private fun cancelTaskNotifications(taskId: String) {
        workManager.cancelAllWorkByTag("${Constants.WORK_TAG_REMINDER}_$taskId")
    }
}

/**
 * UI State for Home Screen
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val upcomingTasks: List<Task>,
        val completedTasks: List<Task>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
