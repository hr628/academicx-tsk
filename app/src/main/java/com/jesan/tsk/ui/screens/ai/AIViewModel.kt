package com.jesan.tsk.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesan.tsk.data.repository.AIRepository
import com.jesan.tsk.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for AI Assistant Screen
 */
@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Hello! I'm your AI study assistant. How can I help you today?",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * Send a message to AI
     */
    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank() || _isLoading.value) return
        
        // Add user message
        _messages.value = _messages.value + ChatMessage(text = userMessage, isUser = true)
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                // Get tasks for context
                val tasks = taskRepository.getUpcomingTasks().first()
                
                // Get AI response
                val result = aiRepository.getAIResponse(userMessage, tasks)
                
                result.onSuccess { response ->
                    _messages.value = _messages.value + ChatMessage(
                        text = response,
                        isUser = false
                    )
                }.onFailure { error ->
                    _messages.value = _messages.value + ChatMessage(
                        text = "I'm having trouble responding right now. Please try again later.",
                        isUser = false
                    )
                }
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    text = "An error occurred. Please try again.",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Get task suggestions
     */
    fun getTaskSuggestions() {
        if (_isLoading.value) return
        
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val tasks = taskRepository.getUpcomingTasks().first()
                
                if (tasks.isEmpty()) {
                    _messages.value = _messages.value + ChatMessage(
                        text = "You don't have any upcoming tasks right now. Great job staying on top of things!",
                        isUser = false
                    )
                } else {
                    val result = aiRepository.getTaskSuggestions(tasks)
                    
                    result.onSuccess { suggestions ->
                        _messages.value = _messages.value + ChatMessage(
                            text = suggestions,
                            isUser = false
                        )
                    }.onFailure { error ->
                        _messages.value = _messages.value + ChatMessage(
                            text = "I couldn't analyze your tasks right now. Please try again later.",
                            isUser = false
                        )
                    }
                }
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    text = "An error occurred. Please try again.",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Data class for chat messages
 */
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)
