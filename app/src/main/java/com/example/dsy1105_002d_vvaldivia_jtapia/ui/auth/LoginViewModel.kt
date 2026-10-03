package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.User
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var emailState = MutableStateFlow("")
        private set

    var passwordState = MutableStateFlow("")
        private set

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        emailState.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        passwordState.value = newPassword
    }

    fun login() {
        val email = emailState.value.trim()
        val password = passwordState.value.trim()

        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Por favor, ingrese email y contraseña.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            // Simulación de respuesta de API REST
            delay(1500)

            val user = when {
                email.contains("admin", ignoreCase = true) -> User(
                    id = "1",
                    name = "Admin General",
                    email = email,
                    role = UserRole.ADMINISTRADOR
                )
                email.contains("super", ignoreCase = true) -> User(
                    id = "2",
                    name = "Supervisor de Turno",
                    email = email,
                    role = UserRole.SUPERVISOR
                )
                else -> User(
                    id = "3",
                    name = "Operador de Cámaras",
                    email = email,
                    role = UserRole.OPERADOR
                )
            }

            _uiState.value = LoginUiState.Success(user)
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}