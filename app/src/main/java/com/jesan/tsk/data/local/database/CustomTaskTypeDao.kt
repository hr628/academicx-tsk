package com.jesan.tsk.data.local.database

import androidx.room.*
import com.jesan.tsk.data.local.entity.CustomTaskTypeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Custom Task Type operations
 */
@Dao
interface CustomTaskTypeDao {
    
    /**
     * Get all custom task types
     */
    @Query("SELECT * FROM custom_task_types ORDER BY name ASC")
    fun getAllCustomTaskTypes(): Flow<List<CustomTaskTypeEntity>>
    
    /**
     * Get a custom task type by ID
     */
    @Query("SELECT * FROM custom_task_types WHERE id = :id")
    suspend fun getCustomTaskTypeById(id: String): CustomTaskTypeEntity?
    
    /**
     * Insert a new custom task type
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomTaskType(customTaskType: CustomTaskTypeEntity)
    
    /**
     * Update an existing custom task type
     */
    @Update
    suspend fun updateCustomTaskType(customTaskType: CustomTaskTypeEntity)
    
    /**
     * Delete a custom task type
     */
    @Delete
    suspend fun deleteCustomTaskType(customTaskType: CustomTaskTypeEntity)
    
    /**
     * Delete all custom task types
     */
    @Query("DELETE FROM custom_task_types")
    suspend fun deleteAllCustomTaskTypes()
}
