package com.example.dsy1105_002d_vvaldivia_jtapia.data.model

enum class UserRole {
    ADMINISTRADOR,
    SUPERVISOR,
    OPERADOR
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole
)