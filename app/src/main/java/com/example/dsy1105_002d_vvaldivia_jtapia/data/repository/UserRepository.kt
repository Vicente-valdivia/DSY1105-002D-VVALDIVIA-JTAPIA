package com.example.dsy1105_002d_vvaldivia_jtapia.data.repository

import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.User
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserRole

// Clase interna auxiliar para guardar usuario y clave juntos
private data class UserRecord(val user: User, val clave: String)

object UserRepository {
    // Lista mutable privada (Nuestra BD simulada)
    private val records = mutableListOf(
        UserRecord(User("1", "Admin General", "admin@modoguardian.cl", UserRole.ADMINISTRADOR), "1234"),
        UserRecord(User("2", "Supervisor de Turno", "super@modoguardian.cl", UserRole.SUPERVISOR), "1234"),
        UserRecord(User("3", "Operador de Cámaras", "operador@modoguardian.cl", UserRole.OPERADOR), "1234")
    )

    // Verifica si el correo ya está registrado
    fun emailExists(email: String): Boolean {
        return records.any { it.user.email == email }
    }

    // Valida credenciales y retorna el usuario si es exitoso
    fun authenticate(email: String, clave: String): User? {
        return records.find { it.user.email == email && it.clave == clave }?.user
    }

    // Registra un nuevo usuario con rol de Operador por defecto
    fun registerUser(nombre: String, apellido: String, email: String, clave: String) {
        val newUser = User(
            id = (records.size + 1).toString(),
            name = "$nombre $apellido",
            email = email,
            role = UserRole.OPERADOR
        )
        records.add(UserRecord(newUser, clave))
    }
}