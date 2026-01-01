package com.jesan.tsk.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesan.tsk.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Settings Screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        val user = authRepository.getCurrentUser()
        _uiState.value = _uiState.value.copy(
            isSignedIn = user != null,
            userEmail = user?.email
        )
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = _uiState.value.copy(
                isSignedIn = false,
                userEmail = null
            )
        }
    }
}

/**
 * UI State for Settings Screen
 */
data class SettingsUiState(
    val isSignedIn: Boolean = false,
    val userEmail: String? = null,
    val darkMode: Boolean = true,
    val notificationsEnabled: Boolean = true
)
