package com.jesan.tsk.data.local.database

import androidx.room.*
import com.jesan.tsk.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Task operations
 */
@Dao
interface TaskDao {
    
    /**
     * Get all tasks ordered by due date (ascending)
     */
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, dueTime ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get upcoming (not completed) tasks ordered by due date
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC, dueTime ASC")
    fun getUpcomingTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get completed tasks ordered by due date (descending)
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY dueDate DESC, dueTime DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get a single task by ID
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?
    
    /**
     * Insert a new task
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
    
    /**
     * Update an existing task
     */
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    /**
     * Delete a task
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    /**
     * Delete a task by ID
     */
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)
    
    /**
     * Mark task as completed/incomplete
     */
    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletionStatus(taskId: String, isCompleted: Boolean)
    
    /**
     * Get tasks for a specific user
     */
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueDate ASC, dueTime ASC")
    fun getTasksByUserId(userId: String): Flow<List<TaskEntity>>
    
    /**
     * Delete all tasks
     */
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
