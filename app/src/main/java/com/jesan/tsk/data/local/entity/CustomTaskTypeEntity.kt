package com.jesan.tsk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jesan.tsk.domain.model.CustomTaskTypeModel

/**
 * Room entity for storing custom task types
 */
@Entity(tableName = "custom_task_types")
data class CustomTaskTypeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val color: String
) {
    /**
     * Convert entity to domain model
     */
    fun toModel(): CustomTaskTypeModel {
        return CustomTaskTypeModel(
            id = id,
            name = name,
            color = color
        )
    }
    
    companion object {
        /**
         * Convert domain model to entity
         */
        fun fromModel(model: CustomTaskTypeModel): CustomTaskTypeEntity {
            return CustomTaskTypeEntity(
                id = model.id,
                name = model.name,
                color = model.color
            )
        }
    }
}
