package com.jesan.tsk.di

import android.content.Context
import androidx.room.Room
import com.jesan.tsk.data.local.database.AppDatabase
import com.jesan.tsk.data.local.database.CustomTaskTypeDao
import com.jesan.tsk.data.local.database.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }
    
    @Provides
    @Singleton
    fun provideCustomTaskTypeDao(database: AppDatabase): CustomTaskTypeDao {
        return database.customTaskTypeDao()
    }
}
