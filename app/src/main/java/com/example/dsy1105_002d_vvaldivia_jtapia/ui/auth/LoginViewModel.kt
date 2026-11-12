package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfile
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginFormState(
    val email: String = "",
    val clave: String = "",
    val errorEmail: String? = null,
    val errorClave: String? = null,
    val mensajeGeneral: String? = null
)

class LoginViewModel : ViewModel() {

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Instancia de Firebase Auth
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onEmailChanged(email: String) {
        _formState.update { it.copy(email = email, errorEmail = null, mensajeGeneral = null) }
    }

    fun onClaveChanged(clave: String) {
        _formState.update { it.copy(clave = clave, errorClave = null, mensajeGeneral = null) }
    }

    // 1. Tu lógica existente de inicio de sesión con correo y contraseña
    fun login() {
        val currentState = _formState.value
        var hasError = false

        val emailTrim = currentState.email.trim()
        val claveTrim = currentState.clave.trim()

        // Validación básica de formato de correo en cliente
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        if (emailTrim.isBlank() || !emailTrim.matches(emailRegex)) {
            _formState.update { it.copy(errorEmail = "Ingrese un correo electrónico válido") }
            hasError = true
        }

        if (claveTrim.isBlank()) {
            _formState.update { it.copy(errorClave = "La contraseña no puede estar vacía") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            delay(600) // Simulación de latencia de red

            // Autenticación en el repositorio
            val user = UserRepository.authenticate(emailTrim, claveTrim)

            if (user != null) {
                // Éxito: Limpiamos formulario y pasamos al estado Success
                _formState.update { LoginFormState() }
                _uiState.value = LoginUiState.Success(user)
            } else {
                // FALLO DE SEGURIDAD: Se borra la contraseña y se muestra mensaje genérico
                _formState.update {
                    it.copy(
                        clave = "", // <-- Borra la contraseña obligatoriamente
                        mensajeGeneral = "Credenciales inválidas. Verifique su correo y clave."
                    )
                }
                _uiState.value = LoginUiState.Idle
            }
        }
    }

    // 2. Función para procesar la autenticación de Google con Firebase y mapear a UserProfile
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val fullName = firebaseUser.displayName ?: "Usuario Google"
                    val email = firebaseUser.email ?: ""

                    // Calculamos las iniciales de forma automática a partir del nombre
                    val initials = fullName.split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString("")
                        .uppercase()
                        .ifEmpty { "UG" }

                    // Creamos el UserProfile con los datos de Firebase y valores por defecto para la app
                    val userProfile = UserProfile(
                        initials = initials,
                        fullName = fullName,
                        roleTitle = "Operador de Guardia",
                        email = email,
                        assignedZone = "Sector Asignado - General",
                        shiftTime = "08:00 - 16:00",
                        totalCameras = 8,
                        resolvedAlertsToday = 0
                    )

                    _formState.update { LoginFormState() }
                    _uiState.value = LoginUiState.Success(userProfile)
                } else {
                    _uiState.value = LoginUiState.Idle
                    _formState.update {
                        it.copy(mensajeGeneral = "No se pudo obtener el usuario de Firebase.")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Idle
                _formState.update {
                    it.copy(mensajeGeneral = "Error al autenticar con Google: ${e.localizedMessage}")
                }
            }
        }
    }
}