package com.example.dsy1105_002d_vvaldivia_jtapia

import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.UserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class UserRepositoryTest : StringSpec({

    "autentica al usuario de prueba por defecto" {
        val user = UserRepository.authenticate("guardian@insercode.cl", "123456")

        user.shouldNotBeNull()
        user.email shouldBe "guardian@insercode.cl"
    }

    "retorna nulo si la contraseña es incorrecta" {
        UserRepository.authenticate("guardian@insercode.cl", "clave_falsa").shouldBeNull()
    }

    "registra un usuario nuevo y permite autenticarlo" {
        val emailNuevo = "test.operador@insercode.cl"

        UserRepository.emailExists(emailNuevo).shouldBeFalse()
        UserRepository.registerUser("Juan", "Pérez", emailNuevo, "abcd1234")
        UserRepository.emailExists(emailNuevo).shouldBeTrue()

        val autenticado = UserRepository.authenticate(emailNuevo, "abcd1234")
        autenticado.shouldNotBeNull()
        autenticado.fullName shouldBe "Juan Pérez"
    }
})
