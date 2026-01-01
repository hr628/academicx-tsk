package com.jesan.tsk.ui.screens.edittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesan.tsk.data.repository.TaskRepository
import com.jesan.tsk.domain.model.Task
import com.jesan.tsk.domain.model.TaskType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Edit Task Screen
 */
@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val taskId: String = savedStateHandle.get<String>("taskId") ?: ""
    
    private val _uiState = MutableStateFlow<EditTaskUiState>(EditTaskUiState.Loading)
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()
    
    init {
        loadTask()
    }
    
    /**
     * Load task from repository
     */
    private fun loadTask() {
        viewModelScope.launch {
            try {
                val task = taskRepository.getTaskById(taskId)
                if (task != null) {
                    _uiState.value = EditTaskUiState.Success(
                        task = task,
                        title = task.title,
                        courseName = task.courseName,
                        taskType = task.taskType,
                        customTaskType = task.customTaskType ?: "",
                        dueDate = task.dueDate,
                        dueTime = task.dueTime,
                        notes = task.notes ?: ""
                    )
                } else {
                    _uiState.value = EditTaskUiState.Error("Task not found")
                }
            } catch (e: Exception) {
                _uiState.value = EditTaskUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Update task title
     */
    fun updateTitle(title: String) {
        val currentState = _uiState.value
        if (currentState is EditTaskUiState.Success) {
            _uiState.value = currentState.copy(title = title, titleError = null)
        }
    }
    
    /**
     * Update course name
     */
    fun updateCourseName(courseName: String) {
        val currentState = _uiState.value
        if (currentState is EditTaskUiState.Success) {
            _uiState.value = currentState.copy(courseName = courseName, courseError = null)
        }
    }
    
    /**
     * Update task type
     */
    fun updateTaskType(taskType: TaskType) {
        val currentState = _uiState.value
        if (currentState is EditTaskUiState.Success) {
            _uiState.value = currentState.copy(taskType = taskType)
        }
    }
    
    /**
     * Update custom task type
     */
    fun updateCustomTaskType(customType: String) {
        val currentState = _uiState.value
        if (currentState is EditTaskUiState.Success) {
            _uiState.value = currentState.copy(customTaskType = customType)
        }
    }
    
    /**
     * Update due date
     */
    fun updateDueDate(timestamp: Long) {
        val currentState = _uiState.value
        if (currentState is EditTaskUiState.Success) {
            _uiState.value = currentState.copy(dueDate = timestamp, dateError = null)
        }
    }
    
    /**
     * Update due time
     */
    fun updateDueTime(time: String) {
        val currentState = _uiState.value
        if (currentState is EditTaskUiState.Success) {
            _uiState.value = currentState.copy(dueTime = time)
        }
    }
    
    /**
     * Update notes
     */
    fun updateNotes(notes: String) {
        val currentState = _uiState.value
        if (currentState is EditTaskUiState.Success) {
            _uiState.value = currentState.copy(notes = notes)
        }
    }
    
    /**
     * Save updated task
     */
    fun saveTask(onSuccess: (Task) -> Unit, onError: () -> Unit) {
        val state = _uiState.value
        if (state !is EditTaskUiState.Success) {
            onError()
            return
        }
        
        // Validation
        var hasError = false
        
        if (state.title.isBlank()) {
            _uiState.value = state.copy(titleError = "Please enter a task title")
            hasError = true
        }
        
        if (state.courseName.isBlank()) {
            _uiState.value = state.copy(courseError = "Please enter a course name")
            hasError = true
        }
        
        if (hasError) {
            onError()
            return
        }
        
        // Update task
        val updatedTask = state.task.copy(
            title = state.title.trim(),
            courseName = state.courseName.trim(),
            taskType = state.taskType,
            customTaskType = if (state.taskType == TaskType.CUSTOM) state.customTaskType else null,
            dueDate = state.dueDate,
            dueTime = state.dueTime,
            notes = state.notes.takeIf { it.isNotBlank() }
        )
        
        viewModelScope.launch {
            try {
                taskRepository.updateTask(updatedTask)
                onSuccess(updatedTask)
            } catch (e: Exception) {
                onError()
            }
        }
    }
    
    /**
     * Delete task
     */
    fun deleteTask(onSuccess: () -> Unit, onError: () -> Unit) {
        val state = _uiState.value
        if (state !is EditTaskUiState.Success) {
            onError()
            return
        }
        
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(state.task)
                onSuccess()
            } catch (e: Exception) {
                onError()
            }
        }
    }
}

/**
 * UI State for Edit Task Screen
 */
sealed class EditTaskUiState {
    object Loading : EditTaskUiState()
    data class Success(
        val task: Task,
        val title: String,
        val courseName: String,
        val taskType: TaskType,
        val customTaskType: String,
        val dueDate: Long,
        val dueTime: String,
        val notes: String,
        
        // Error states
        val titleError: String? = null,
        val courseError: String? = null,
        val dateError: String? = null
    ) : EditTaskUiState()
    data class Error(val message: String) : EditTaskUiState()
}
