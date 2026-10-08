package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.User
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.UserRepository // Importamos tu repositorio object
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Data class para el formulario
data class LoginFormState(
    val email: String = "",
    val clave: String = "",
    val errorEmail: String? = null,
    val errorClave: String? = null
)

class LoginViewModel : ViewModel() {

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

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
        val emailInput = currentForm.email.trim().lowercase()
        val password = currentForm.clave.trim()

        // Validación de campos vacíos
        if (emailInput.isBlank() || password.isBlank()) {
            _formState.update {
                it.copy(
                    email = "", clave = "",
                    errorEmail = if (emailInput.isBlank()) "El correo es obligatorio" else "Credenciales incorrectas",
                    errorClave = if (password.isBlank()) "La contraseña es obligatoria" else "Credenciales incorrectas"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            delay(1000) // Simulación de red

            // CORREGIDO: Al ser un 'object', lo llamamos directamente usando la mayúscula 'UserRepository'
            val user = UserRepository.authenticate(emailInput, password)

            if (user != null) {
                // Login Exitoso
                _formState.update { LoginFormState() } // Limpiamos formulario
                _uiState.value = LoginUiState.Success(user)
            } else {
                // Login Fallido (No existe o clave incorrecta)
                _formState.update {
                    it.copy(
                        email = "", clave = "",
                        errorEmail = "Credenciales incorrectas",
                        errorClave = "Credenciales incorrectas"
                    )
                }
                _uiState.value = LoginUiState.Idle
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
        _formState.update { LoginFormState() }
    }
}
