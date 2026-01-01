package com.jesan.tsk.data.repository

import com.jesan.tsk.data.local.database.CustomTaskTypeDao
import com.jesan.tsk.data.local.database.TaskDao
import com.jesan.tsk.data.local.entity.CustomTaskTypeEntity
import com.jesan.tsk.data.local.entity.TaskEntity
import com.jesan.tsk.domain.model.CustomTaskTypeModel
import com.jesan.tsk.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for task-related operations
 * Handles local database operations and potential cloud sync
 */
@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val customTaskTypeDao: CustomTaskTypeDao
) {
    
    /**
     * Get all tasks as Flow
     */
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toTask() }
        }
    }
    
    /**
     * Get upcoming tasks as Flow
     */
    fun getUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getUpcomingTasks().map { entities ->
            entities.map { it.toTask() }
        }
    }
    
    /**
     * Get completed tasks as Flow
     */
    fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks().map { entities ->
            entities.map { it.toTask() }
        }
    }
    
    /**
     * Get task by ID
     */
    suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)?.toTask()
    }
    
    /**
     * Insert a new task
     */
    suspend fun insertTask(task: Task) {
        taskDao.insertTask(TaskEntity.fromTask(task))
    }
    
    /**
     * Update an existing task
     */
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(TaskEntity.fromTask(task))
    }
    
    /**
     * Delete a task
     */
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(TaskEntity.fromTask(task))
    }
    
    /**
     * Delete task by ID
     */
    suspend fun deleteTaskById(taskId: String) {
        taskDao.deleteTaskById(taskId)
    }
    
    /**
     * Toggle task completion status
     */
    suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        taskDao.updateTaskCompletionStatus(taskId, isCompleted)
    }
    
    /**
     * Get all custom task types
     */
    fun getAllCustomTaskTypes(): Flow<List<CustomTaskTypeModel>> {
        return customTaskTypeDao.getAllCustomTaskTypes().map { entities ->
            entities.map { it.toModel() }
        }
    }
    
    /**
     * Insert custom task type
     */
    suspend fun insertCustomTaskType(customType: CustomTaskTypeModel) {
        customTaskTypeDao.insertCustomTaskType(CustomTaskTypeEntity.fromModel(customType))
    }
    
    /**
     * Delete custom task type
     */
    suspend fun deleteCustomTaskType(customType: CustomTaskTypeModel) {
        customTaskTypeDao.deleteCustomTaskType(CustomTaskTypeEntity.fromModel(customType))
    }
}
