package com.example.learningdashboard.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.domain.model.LoginValidator
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.domain.repository.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false,
    /** Server-side rejection, shown separately from field validation. */
    val loginError: String? = null,
) {
    val canSubmit: Boolean get() = !isSubmitting
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, emailError = null, loginError = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, passwordError = null, loginError = null) }

    fun submit() {
        val current = _uiState.value
        val emailError = LoginValidator.emailError(current.email)
        val passwordError = LoginValidator.passwordError(current.password)
        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, loginError = null) }
            val result = authRepository.login(current.email.trim(), current.password)
            _uiState.update {
                when (result) {
                    LoginResult.Success -> it.copy(isSubmitting = false)
                    is LoginResult.Failure -> it.copy(isSubmitting = false, loginError = result.message)
                }
            }
        }
    }
}
