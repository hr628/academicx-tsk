package com.jesan.tsk.ui.screens.edittask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
 * Edit Task Screen for modifying existing tasks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    onNavigateBack: () -> Unit,
    homeViewModel: HomeViewModel,
    viewModel: EditTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Task",
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
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error
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
        when (val state = uiState) {
            is EditTaskUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is EditTaskUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            is EditTaskUiState.Success -> {
                EditTaskForm(
                    state = state,
                    viewModel = viewModel,
                    homeViewModel = homeViewModel,
                    onNavigateBack = onNavigateBack,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(
                            onSuccess = {
                                showDeleteDialog = false
                                Toast.makeText(
                                    context,
                                    "Task deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onNavigateBack()
                            },
                            onError = {
                                showDeleteDialog = false
                                Toast.makeText(
                                    context,
                                    "Error deleting task",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskForm(
    state: EditTaskUiState.Success,
    viewModel: EditTaskViewModel,
    homeViewModel: HomeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Task Title
        OutlinedTextField(
            value = state.title,
            onValueChange = { viewModel.updateTitle(it) },
            label = { Text("Task Title") },
            isError = state.titleError != null,
            supportingText = state.titleError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Course Name
        OutlinedTextField(
            value = state.courseName,
            onValueChange = { viewModel.updateCourseName(it) },
            label = { Text("Course Name") },
            isError = state.courseError != null,
            supportingText = state.courseError?.let { { Text(it) } },
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
                value = state.taskType.displayName,
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
        if (state.taskType == TaskType.CUSTOM) {
            OutlinedTextField(
                value = state.customTaskType,
                onValueChange = { viewModel.updateCustomTaskType(it) },
                label = { Text("Custom Type Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        
        // Due Date Picker
        OutlinedButton(
            onClick = {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = state.dueDate
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
            Text(text = "Due Date: ${DateUtils.formatDate(state.dueDate)}")
        }
        
        // Due Time Picker
        OutlinedButton(
            onClick = {
                val timeParts = state.dueTime.split(":")
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
            Text(text = "Due Time: ${state.dueTime}")
        }
        
        // Notes
        OutlinedTextField(
            value = state.notes,
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
                            // Reschedule notifications
                            homeViewModel.scheduleTaskNotifications(task)
                            
                            Toast.makeText(
                                context,
                                "Task updated successfully",
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
                Text("Save")
            }
        }
    }
}
