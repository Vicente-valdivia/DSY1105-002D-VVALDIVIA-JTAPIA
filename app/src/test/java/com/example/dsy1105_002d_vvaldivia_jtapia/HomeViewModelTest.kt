package com.example.dsy1105_002d_vvaldivia_jtapia

import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.Camera
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.CameraStatus
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.EventSeverity
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.SecurityEvent
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfile
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.SecurityRepository
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens.HomeViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest : StringSpec({

    // viewModelScope necesita un Dispatchers.Main; en tests JVM hay que proveerlo
    beforeSpec { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterSpec { Dispatchers.resetMain() }

    val cameras = listOf(
        Camera("C1", "A", "Z", CameraStatus.RECORDING, timestamp = "1"),
        Camera("C2", "B", "Z", CameraStatus.ONLINE, timestamp = "2"),
        Camera("C3", "C", "Z", CameraStatus.OFFLINE, timestamp = "3"),
        Camera("C4", "D", "Z", CameraStatus.OFFLINE, timestamp = "4")
    )

    "buildState calcula cámaras activas y capacidad operativa reales" {
        val state = HomeViewModel.buildState(UserProfile(), cameras, emptyList())

        state.activeCameras shouldBe 2
        state.operationalRatio shouldBe 0.5f
    }

    "buildState no divide por cero sin cámaras" {
        HomeViewModel.buildState(UserProfile(), emptyList(), emptyList()).operationalRatio shouldBe 0f
    }

    "las alertas pendientes ignoran eventos informativos y confirmados" {
        val events = listOf(
            SecurityEvent("1", "t", "l", "10:00", EventSeverity.CRITICAL),
            SecurityEvent("2", "t", "l", "10:01", EventSeverity.WARNING, isConfirmed = true),
            SecurityEvent("3", "t", "l", "10:02", EventSeverity.INFO)
        )
        HomeViewModel.buildState(UserProfile(), cameras, events).pendingAlerts shouldBe 1
    }

    "buildState usa valores por defecto si el perfil está vacío" {
        val state = HomeViewModel.buildState(UserProfile(), cameras, emptyList())
        state.displayName shouldBe "Guardián"
        state.initials shouldBe "VG"
    }

    "reportIncident rechaza títulos demasiado cortos" {
        val before = SecurityRepository.events.value.size
        HomeViewModel().reportIncident("ab", "", EventSeverity.WARNING) shouldBe false
        SecurityRepository.events.value.size shouldBe before
    }

    "reportIncident agrega el evento al inicio con id nuevo" {
        val before = SecurityRepository.events.value
        HomeViewModel().reportIncident("Puerta abierta", "Bodega A", EventSeverity.CRITICAL, "CAM-03") shouldBe true

        val after = SecurityRepository.events.value
        after.size shouldBe before.size + 1
        after.first().title shouldBe "Puerta abierta"
        after.map { it.id }.toSet().size shouldBe after.size // ids únicos
    }

    "confirmEvent marca el evento como atendido" {
        val id = SecurityRepository.addEvent("Prueba", "x", EventSeverity.WARNING).id
        HomeViewModel().confirmEvent(id)
        SecurityRepository.events.value.first { it.id == id }.isConfirmed shouldBe true
    }
})
