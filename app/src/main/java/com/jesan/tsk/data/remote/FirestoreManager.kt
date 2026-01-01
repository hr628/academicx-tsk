package com.jesan.tsk.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.jesan.tsk.domain.model.CustomTaskTypeModel
import com.jesan.tsk.domain.model.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for Firestore operations (Optional feature for cloud sync)
 */
@Singleton
class FirestoreManager @Inject constructor() {
    
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    /**
     * Upload task to Firestore
     */
    suspend fun uploadTask(userId: String, task: Task): Result<Unit> {
        return try {
            val taskData = hashMapOf(
                "id" to task.id,
                "title" to task.title,
                "courseName" to task.courseName,
                "taskType" to task.taskType.name,
                "customTaskType" to task.customTaskType,
                "dueDate" to task.dueDate,
                "dueTime" to task.dueTime,
                "notes" to task.notes,
                "isCompleted" to task.isCompleted,
                "createdAt" to task.createdAt
            )
            
            firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .document(task.id)
                .set(taskData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete task from Firestore
     */
    suspend fun deleteTask(userId: String, taskId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)
                .delete()
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Listen to user's tasks in Firestore
     * TODO: Implement proper Firestore document to Task conversion when enabling cloud sync
     */
    fun listenToTasks(userId: String): Flow<List<Task>> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                // TODO: Implement proper document-to-Task conversion
                // For now, this returns an empty list as cloud sync is optional
                val tasks = emptyList<Task>()
                
                trySend(tasks)
            }
        
        awaitClose { listener.remove() }
    }
    
    /**
     * Upload custom task type to Firestore
     */
    suspend fun uploadCustomTaskType(userId: String, customType: CustomTaskTypeModel): Result<Unit> {
        return try {
            val typeData = hashMapOf(
                "id" to customType.id,
                "name" to customType.name,
                "color" to customType.color
            )
            
            firestore.collection("users")
                .document(userId)
                .collection("customTaskTypes")
                .document(customType.id)
                .set(typeData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
