package com.example.dsy1105_002d_vvaldivia_jtapia.data.model

/** Construye el perfil de la app a partir de los datos de una cuenta de Firebase. */
object UserProfileFactory {

    fun create(displayName: String?, email: String): UserProfile {
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
}
