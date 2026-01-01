package com.jesan.tsk.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Enum representing predefined task types with associated colors
 */
enum class TaskType(val displayName: String, val colorHex: String) {
    QUIZ_1("Quiz 1", "#3B82F6"),
    QUIZ_2("Quiz 2", "#3B82F6"),
    QUIZ_3("Quiz 3", "#3B82F6"),
    EXTRA_QUIZ("Extra Quiz", "#3B82F6"),
    ASSIGNMENT("Assignment", "#F97316"),
    MIDTERM("Midterm", "#EF4444"),
    FINAL("Final", "#EF4444"),
    PROJECT("Project", "#8B5CF6"),
    PRESENTATION("Presentation", "#10B981"),
    CUSTOM("Custom", "#6366F1");
    
    companion object {
        fun fromDisplayName(name: String): TaskType {
            return values().find { it.displayName == name } ?: CUSTOM
        }
    }
}

/**
 * Data class for custom task types
 */
data class CustomTaskTypeModel(
    val id: String,
    val name: String,
    val color: String
)
