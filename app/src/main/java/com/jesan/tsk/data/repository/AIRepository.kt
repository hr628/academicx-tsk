package com.jesan.tsk.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.jesan.tsk.BuildConfig
import com.jesan.tsk.domain.model.Task
import com.jesan.tsk.utils.Constants
import com.jesan.tsk.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for AI operations using Gemini API
 */
@Singleton
class AIRepository @Inject constructor() {
    
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }
    
    /**
     * Get AI response for user query with task context
     */
    suspend fun getAIResponse(userMessage: String, tasks: List<Task>): Result<String> {
        return try {
            // Build context from tasks
            val taskContext = buildTaskContext(tasks)
            
            // Build prompt
            val prompt = """
                ${Constants.AI_SYSTEM_PROMPT}
                
                Here are the user's current tasks:
                $taskContext
                
                User's question: $userMessage
                
                Please provide a helpful, concise response.
            """.trimIndent()
            
            // Get response from Gemini
            val response = generativeModel.generateContent(prompt)
            val text = response.text ?: "I couldn't generate a response. Please try again."
            
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get AI suggestions based on tasks
     */
    suspend fun getTaskSuggestions(tasks: List<Task>): Result<String> {
        return try {
            val taskContext = buildTaskContext(tasks)
            
            val prompt = """
                ${Constants.AI_SYSTEM_PROMPT}
                
                Here are the user's current tasks:
                $taskContext
                
                Analyze these tasks and provide:
                1. Priority recommendations (which tasks to focus on first)
                2. Time management suggestions
                3. Any potential conflicts or concerns
                
                Keep the response concise and actionable.
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            val text = response.text ?: "No suggestions available at the moment."
            
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Build context string from tasks
     */
    private fun buildTaskContext(tasks: List<Task>): String {
        if (tasks.isEmpty()) {
            return "No tasks currently scheduled."
        }
        
        val upcomingTasks = tasks.filter { !it.isCompleted }
            .sortedBy { it.dueDate }
            .take(10) // Limit to top 10 upcoming tasks
        
        return upcomingTasks.joinToString("\n") { task ->
            val date = DateUtils.formatDate(task.dueDate)
            val urgency = DateUtils.getUrgencyLabel(task.dueDate) ?: ""
            val urgencyStr = if (urgency.isNotEmpty()) " ($urgency)" else ""
            
            "- ${task.title} (${task.courseName}) - ${task.taskType.displayName} - Due: $date${urgencyStr}"
        }
    }
}
