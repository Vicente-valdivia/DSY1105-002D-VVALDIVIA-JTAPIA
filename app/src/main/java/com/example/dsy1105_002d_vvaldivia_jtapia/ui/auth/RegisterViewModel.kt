package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfile
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.SecurityRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RegisterFormState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val errorNombre: String? = null,
    val errorApellido: String? = null,
    val errorEmail: String? = null,
    val errorClave: String? = null,
    val errorConfirmar: String? = null,
    val registroExitoso: Boolean = false,
    val isLoading: Boolean = false
)

class RegisterViewModel : ViewModel() {

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState.asStateFlow()

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onNombreChanged(v: String) { _formState.update { it.copy(nombre = v, errorNombre = null) } }
    fun onApellidoChanged(v: String) { _formState.update { it.copy(apellido = v, errorApellido = null) } }
    fun onEmailChanged(v: String) { _formState.update { it.copy(email = v, errorEmail = null) } }
    fun onClaveChanged(v: String) { _formState.update { it.copy(clave = v, errorClave = null) } }
    fun onConfirmarClaveChanged(v: String) { _formState.update { it.copy(confirmarClave = v, errorConfirmar = null) } }

    fun registrar() {
        val state = _formState.value
        var hasError = false

        val nombreTrim = state.nombre.trim()
        val apellidoTrim = state.apellido.trim()
        val emailTrim = state.email.trim()
        val claveTrim = state.clave.trim()
        val confirmarTrim = state.confirmarClave.trim()

        if (nombreTrim.length < 2) {
            _formState.update { it.copy(errorNombre = "El nombre debe tener al menos 2 caracteres") }
            hasError = true
        }

        if (apellidoTrim.length < 2) {
            _formState.update { it.copy(errorApellido = "El apellido debe tener al menos 2 caracteres") }
            hasError = true
        }

        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!emailTrim.matches(emailRegex)) {
            _formState.update { it.copy(errorEmail = "Formato de correo inválido (ej: usuario@dominio.cl)") }
            hasError = true
        }

        if (claveTrim.length < 6) {
            _formState.update { it.copy(errorClave = "La contraseña debe tener un mínimo de 6 caracteres") }
            hasError = true
        }

        if (claveTrim != confirmarTrim) {
            _formState.update { it.copy(errorConfirmar = "Las contraseñas no coinciden") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(emailTrim, claveTrim).await()
                val fullName = "$nombreTrim $apellidoTrim"

                // Guardamos el nombre completo en el perfil de Firebase Auth
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()
                authResult.user?.updateProfile(profileUpdates)?.await()

                val initials = listOfNotNull(
                    nombreTrim.firstOrNull(),
                    apellidoTrim.firstOrNull()
                ).joinToString("").uppercase().ifEmpty { "VG" }

                SecurityRepository.currentUser = UserProfile(
                    initials = initials,
                    fullName = fullName,
                    roleTitle = "Guardián de Seguridad",
                    email = emailTrim,
                    assignedZone = "Guardián • Zona Sur",
                    shiftTime = "08:00 - 16:00",
                    totalCameras = 12,
                    resolvedAlertsToday = 0
                )

                _formState.update { it.copy(isLoading = false, registroExitoso = true) }
            } catch (e: Exception) {
                _formState.update {
                    it.copy(
                        isLoading = false,
                        errorEmail = e.localizedMessage ?: "No se pudo completar el registro en Firebase"
                    )
                }
            }
        }
    }
}