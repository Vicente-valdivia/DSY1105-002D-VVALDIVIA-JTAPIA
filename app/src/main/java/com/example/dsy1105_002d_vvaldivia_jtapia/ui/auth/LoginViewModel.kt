package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfile
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.SecurityRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onEmailChanged(email: String) {
        _formState.update { it.copy(email = email, errorEmail = null, mensajeGeneral = null) }
    }

    fun onClaveChanged(clave: String) {
        _formState.update { it.copy(clave = clave, errorClave = null, mensajeGeneral = null) }
    }

    private fun buildUserProfile(displayName: String?, email: String): UserProfile {
        val fallbackName = email.substringBefore("@")
            .replace(".", " ")
            .replace("_", " ")
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { part -> part.replaceFirstChar { it.uppercase() } }
            .ifBlank { "Operador" }

        val fullName = displayName?.takeIf { it.isNotBlank() } ?: fallbackName

        val initials = fullName.split(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()
            .ifEmpty { "VG" }

        return UserProfile(
            initials = initials,
            fullName = fullName,
            roleTitle = "Guardián de Seguridad",
            email = email,
            assignedZone = "Guardián • Zona Sur",
            shiftTime = "08:00 - 16:00",
            totalCameras = 12,
            resolvedAlertsToday = 0
        )
    }

    // 1. Inicio de sesión con correo y contraseña en Firebase Auth
    fun login() {
        val currentState = _formState.value
        var hasError = false

        val emailTrim = currentState.email.trim()
        val claveTrim = currentState.clave.trim()

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
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(emailTrim, claveTrim).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val userProfile = buildUserProfile(
                        displayName = firebaseUser.displayName,
                        email = firebaseUser.email ?: emailTrim
                    )
                    SecurityRepository.currentUser = userProfile
                    _formState.update { LoginFormState() }
                    _uiState.value = LoginUiState.Success(userProfile)
                } else {
                    _uiState.value = LoginUiState.Idle
                    _formState.update {
                        it.copy(mensajeGeneral = "No se pudo obtener la sesión del usuario.")
                    }
                }
            } catch (e: Exception) {
                _formState.value = itCopyForFailedLogin(
                    currentState = _formState.value,
                    message = "Correo o contraseña incorrectos."
                )
                _uiState.value = LoginUiState.Idle
            }
        }
    }

    private fun itCopyForFailedLogin(currentState: LoginFormState, message: String): LoginFormState {
        return currentState.copy(clave = "", mensajeGeneral = message)
    }

    // 2. Inicio de sesión con Google en Firebase Auth
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val userProfile = buildUserProfile(
                        displayName = firebaseUser.displayName,
                        email = firebaseUser.email ?: ""
                    )
                    SecurityRepository.currentUser = userProfile
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