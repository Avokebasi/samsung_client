package com.cattery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cattery.domain.models.UserRole
import com.cattery.domain.usecases.AuthUseCases
import com.cattery.presentation.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)

class AuthViewModel(
    private val authUseCases: AuthUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authUseCases.login(username.trim(), password)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.userMessage("Ошибка входа"),
                        )
                    }
                }
        }
    }

    fun register(name: String, username: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authUseCases.register(name.trim(), username.trim(), password, role)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.userMessage("Ошибка регистрации"),
                        )
                    }
                }
        }
    }

    fun clearState() {
        _uiState.value = AuthUiState()
    }
}
