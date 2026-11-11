package com.example.dsy1105_002d_vvaldivia_jtapia

import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserRole
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.UserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class UserRepositoryTest : StringSpec({

    "debe autenticar correctamente al administrador por defecto" {
        val user = UserRepository.authenticate("admin@modoguardian.cl", "1234")

        user.shouldNotBeNull()
        user.role shouldBe UserRole.ADMINISTRADOR
        user.email shouldBe "admin@modoguardian.cl"
    }

    "debe retornar nulo si la contraseña es incorrecta" {
        val user = UserRepository.authenticate("admin@modoguardian.cl", "clave_falsa")
        user.shouldBeNull()
    }

    "debe registrar un nuevo usuario y permitir autenticarlo" {
        val emailNuevo = "test.operador@modoguardian.cl"

        UserRepository.emailExists(emailNuevo).shouldBeFalse()

        UserRepository.registerUser("Juan", "Pérez", emailNuevo, "abcd1234")

        UserRepository.emailExists(emailNuevo).shouldBeTrue()

        val userAutenticado = UserRepository.authenticate(emailNuevo, "abcd1234")
        userAutenticado.shouldNotBeNull()
        userAutenticado.name shouldBe "Juan Pérez"
        userAutenticado.role shouldBe UserRole.OPERADOR
    }
})