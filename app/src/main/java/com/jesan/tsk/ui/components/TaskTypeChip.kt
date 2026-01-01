package com.jesan.tsk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jesan.tsk.domain.model.TaskType
import com.jesan.tsk.ui.theme.*

/**
 * Chip component for displaying task type with colored background
 */
@Composable
fun TaskTypeChip(
    taskType: TaskType,
    customType: String?,
    modifier: Modifier = Modifier
) {
    val displayName = if (taskType == TaskType.CUSTOM && !customType.isNullOrEmpty()) {
        customType
    } else {
        taskType.displayName
    }
    
    val chipColor = getTaskTypeColor(taskType)
    
    Box(
        modifier = modifier
            .background(
                color = chipColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = displayName,
            style = MaterialTheme.typography.labelMedium,
            color = chipColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Get color for task type
 */
fun getTaskTypeColor(taskType: TaskType): Color {
    return when (taskType) {
        TaskType.QUIZ_1, TaskType.QUIZ_2, TaskType.QUIZ_3, TaskType.EXTRA_QUIZ -> QuizBlue
        TaskType.ASSIGNMENT -> AssignmentOrange
        TaskType.MIDTERM, TaskType.FINAL -> ExamRed
        TaskType.PROJECT -> ProjectPurple
        TaskType.PRESENTATION -> PresentationGreen
        TaskType.CUSTOM -> Indigo
    }
}
