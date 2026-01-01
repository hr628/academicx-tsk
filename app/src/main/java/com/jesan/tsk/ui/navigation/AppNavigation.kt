package com.jesan.tsk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jesan.tsk.ui.screens.addtask.AddTaskScreen
import com.jesan.tsk.ui.screens.ai.AIAssistantScreen
import com.jesan.tsk.ui.screens.edittask.EditTaskScreen
import com.jesan.tsk.ui.screens.home.HomeScreen
import com.jesan.tsk.ui.screens.home.HomeViewModel
import com.jesan.tsk.ui.screens.settings.SettingsScreen

/**
 * Navigation routes for the app
 */
object Routes {
    const val HOME = "home"
    const val ADD_TASK = "add_task"
    const val EDIT_TASK = "edit_task/{taskId}"
    const val AI_ASSISTANT = "ai_assistant"
    const val SETTINGS = "settings"
    
    fun editTask(taskId: String) = "edit_task/$taskId"
}

/**
 * App Navigation component
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Get HomeViewModel at the navigation level to share across screens
    val homeViewModel: HomeViewModel = hiltViewModel()
    
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        // Home Screen
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToAddTask = {
                    navController.navigate(Routes.ADD_TASK)
                },
                onNavigateToEditTask = { taskId ->
                    navController.navigate(Routes.editTask(taskId))
                },
                onNavigateToAI = {
                    navController.navigate(Routes.AI_ASSISTANT)
                },
                viewModel = homeViewModel
            )
        }
        
        // Add Task Screen
        composable(Routes.ADD_TASK) {
            AddTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                homeViewModel = homeViewModel
            )
        }
        
        // Edit Task Screen
        composable(
            route = Routes.EDIT_TASK,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                }
            )
        ) {
            EditTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                homeViewModel = homeViewModel
            )
        }
        
        // AI Assistant Screen
        composable(Routes.AI_ASSISTANT) {
            AIAssistantScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Settings Screen
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
