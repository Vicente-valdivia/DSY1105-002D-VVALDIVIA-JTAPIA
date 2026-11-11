package com.example.dsy1105_002d_vvaldivia_jtapia.data.repository

import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Estructura interna para almacenar usuario con credenciales
private data class RegisteredUser(
    val profile: UserProfile,
    val clave: String
)

object UserRepository {
    // Almacén mutable con credenciales completas
    private val registeredUsers = mutableListOf(
        RegisteredUser(
            profile = UserProfile(
                fullName = "Víctor P.",
                email = "guardian@insercode.cl",
                assignedZone = "Sector Norte - Central",
                shiftTime = "14:00 - 22:00",
                totalCameras = 12,
                initials = "VP",
                roleTitle = "Guardia Senior",
                resolvedAlertsToday = 4
            ),
            clave = "123456" // Contraseña por defecto para pruebas
        )
    )

    private val _currentUser = MutableStateFlow(registeredUsers.first().profile)
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Autenticación ESTRICTA: Exige concordancia exacta de Correo Y Contraseña
    fun authenticate(email: String, clave: String): UserProfile? {
        val foundUser = registeredUsers.find {
            it.profile.email.equals(email.trim(), ignoreCase = true) && it.clave == clave.trim()
        }

        if (foundUser != null) {
            _currentUser.value = foundUser.profile
            return foundUser.profile
        }
        return null
    }

    fun login(email: String): Boolean {
        return emailExists(email)
    }

    fun emailExists(email: String): Boolean {
        return registeredUsers.any { it.profile.email.equals(email.trim(), ignoreCase = true) }
    }

    // El registro guarda al usuario PERO NO inicia sesión automáticamente
    fun registerUser(nombre: String, apellido: String, email: String, clave: String): UserProfile {
        val fullName = "$nombre $apellido".trim()
        val calculatedInitials = fullName.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()

        val newProfile = UserProfile(
            fullName = fullName,
            email = email.trim(),
            assignedZone = "Sector Asignado - General",
            shiftTime = "08:00 - 16:00",
            totalCameras = 8,
            initials = calculatedInitials.ifEmpty { "G" },
            roleTitle = "Operador de Guardia",
            resolvedAlertsToday = 0
        )

        registeredUsers.add(RegisteredUser(profile = newProfile, clave = clave.trim()))
        // NOTA: No actualizamos _currentUser.value aquí para exigir el login posterior.
        return newProfile
    }

    fun register(fullName: String, email: String): UserProfile {
        return registerUser(fullName, "", email, "123456")
    }
}