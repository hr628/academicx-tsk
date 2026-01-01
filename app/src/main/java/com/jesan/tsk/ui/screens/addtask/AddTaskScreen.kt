package com.jesan.tsk.ui.screens.addtask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jesan.tsk.domain.model.TaskType
import com.jesan.tsk.ui.screens.home.HomeViewModel
import com.jesan.tsk.ui.theme.TextPrimary
import com.jesan.tsk.utils.DateUtils
import java.util.Calendar

/**
 * Add Task Screen for creating new tasks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit,
    homeViewModel: HomeViewModel,
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Task",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Title
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Task Title") },
                isError = uiState.titleError != null,
                supportingText = uiState.titleError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Course Name
            OutlinedTextField(
                value = uiState.courseName,
                onValueChange = { viewModel.updateCourseName(it) },
                label = { Text("Course Name") },
                isError = uiState.courseError != null,
                supportingText = uiState.courseError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Task Type Dropdown
            var expanded by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.taskType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Task Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    TaskType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                viewModel.updateTaskType(type)
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // Custom Task Type (if Custom is selected)
            if (uiState.taskType == TaskType.CUSTOM) {
                OutlinedTextField(
                    value = uiState.customTaskType,
                    onValueChange = { viewModel.updateCustomTaskType(it) },
                    label = { Text("Custom Type Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            // Due Date Picker
            OutlinedButton(
                onClick = {
                    val calendar = Calendar.getInstance()
                    if (uiState.dueDate != null) {
                        calendar.timeInMillis = uiState.dueDate!!
                    }
                    
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val selectedCalendar = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            }
                            viewModel.updateDueDate(selectedCalendar.timeInMillis)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = uiState.dueDate?.let { DateUtils.formatDate(it) } ?: "Select Due Date"
                )
            }
            
            if (uiState.dateError != null) {
                Text(
                    text = uiState.dateError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Due Time Picker
            OutlinedButton(
                onClick = {
                    val timeParts = uiState.dueTime.split(":")
                    val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 9
                    val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
                    
                    TimePickerDialog(
                        context,
                        { _, selectedHour, selectedMinute ->
                            val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                            viewModel.updateDueTime(timeString)
                        },
                        hour,
                        minute,
                        true
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Due Time: ${uiState.dueTime}")
            }
            
            // Notes
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.updateNotes(it) },
                label = { Text("Notes (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = {
                        viewModel.saveTask(
                            onSuccess = { task ->
                                // Schedule notifications
                                homeViewModel.scheduleTaskNotifications(task)
                                
                                Toast.makeText(
                                    context,
                                    "Task added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onNavigateBack()
                            },
                            onError = {
                                Toast.makeText(
                                    context,
                                    "Please fill all required fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Task")
                }
            }
        }
    }
}
