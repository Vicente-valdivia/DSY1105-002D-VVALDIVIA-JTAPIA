package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.User
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Data class para el formulario (alineado a la Guía 11)
data class LoginFormState(
    val email: String = "",
    val clave: String = "",
    val errorEmail: String? = null,
    val errorClave: String? = null
)

class LoginViewModel : ViewModel() {

    // Estado del formulario encapsulado
    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    // Estado de la pantalla (Loading, Success, Error)
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _formState.update { it.copy(email = newEmail, errorEmail = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _formState.update { it.copy(clave = newPassword, errorClave = null) }
    }

    fun login() {
        val currentForm = _formState.value
        val email = currentForm.email.trim()
        val password = currentForm.clave.trim()

        var hasError = false

        // Validaciones individuales
        if (email.isBlank()) {
            _formState.update { it.copy(errorEmail = "El correo es obligatorio") }
            hasError = true
        }
        if (password.isBlank()) {
            _formState.update { it.copy(errorClave = "La contraseña es obligatoria") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            delay(1500) // Simulación de API

            val user = when {
                email.contains("admin", ignoreCase = true) -> User("1", "Admin General", email, UserRole.ADMINISTRADOR)
                email.contains("super", ignoreCase = true) -> User("2", "Supervisor de Turno", email, UserRole.SUPERVISOR)
                else -> User("3", "Operador de Cámaras", email, UserRole.OPERADOR)
            }

            _uiState.value = LoginUiState.Success(user)
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
        _formState.update { LoginFormState() } // Limpia los campos
    }
}