package com.jesan.tsk.ui.screens.addtask

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
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for Add Task Screen
 */
@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()
    
    /**
     * Update task title
     */
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title, titleError = null)
    }
    
    /**
     * Update course name
     */
    fun updateCourseName(courseName: String) {
        _uiState.value = _uiState.value.copy(courseName = courseName, courseError = null)
    }
    
    /**
     * Update task type
     */
    fun updateTaskType(taskType: TaskType) {
        _uiState.value = _uiState.value.copy(taskType = taskType, taskTypeError = null)
    }
    
    /**
     * Update custom task type name
     */
    fun updateCustomTaskType(customType: String) {
        _uiState.value = _uiState.value.copy(customTaskType = customType)
    }
    
    /**
     * Update due date (timestamp)
     */
    fun updateDueDate(timestamp: Long) {
        _uiState.value = _uiState.value.copy(dueDate = timestamp, dateError = null)
    }
    
    /**
     * Update due time
     */
    fun updateDueTime(time: String) {
        _uiState.value = _uiState.value.copy(dueTime = time)
    }
    
    /**
     * Update notes
     */
    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }
    
    /**
     * Validate and save task
     */
    fun saveTask(onSuccess: (Task) -> Unit, onError: () -> Unit) {
        val state = _uiState.value
        
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
        
        if (state.dueDate == null) {
            _uiState.value = state.copy(dateError = "Please select a due date")
            hasError = true
        }
        
        if (hasError) {
            onError()
            return
        }
        
        // Create task
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = state.title.trim(),
            courseName = state.courseName.trim(),
            taskType = state.taskType,
            customTaskType = if (state.taskType == TaskType.CUSTOM) state.customTaskType else null,
            dueDate = state.dueDate!!,
            dueTime = state.dueTime,
            notes = state.notes.takeIf { it.isNotBlank() },
            isCompleted = false
        )
        
        // Save to repository
        viewModelScope.launch {
            try {
                taskRepository.insertTask(task)
                onSuccess(task)
            } catch (e: Exception) {
                onError()
            }
        }
    }
}

/**
 * UI State for Add Task Screen
 */
data class AddTaskUiState(
    val title: String = "",
    val courseName: String = "",
    val taskType: TaskType = TaskType.QUIZ_1,
    val customTaskType: String = "",
    val dueDate: Long? = null,
    val dueTime: String = "09:00",
    val notes: String = "",
    
    // Error states
    val titleError: String? = null,
    val courseError: String? = null,
    val taskTypeError: String? = null,
    val dateError: String? = null
)
