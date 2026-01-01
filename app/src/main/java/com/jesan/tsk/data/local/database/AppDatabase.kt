package com.jesan.tsk.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jesan.tsk.data.local.entity.CustomTaskTypeEntity
import com.jesan.tsk.data.local.entity.TaskEntity

/**
 * Room Database for the app
 */
@Database(
    entities = [
        TaskEntity::class,
        CustomTaskTypeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun taskDao(): TaskDao
    abstract fun customTaskTypeDao(): CustomTaskTypeDao
    
    companion object {
        const val DATABASE_NAME = "academicx_tsk_database"
    }
}
