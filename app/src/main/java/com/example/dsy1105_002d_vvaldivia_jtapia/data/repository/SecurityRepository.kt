package com.example.dsy1105_002d_vvaldivia_jtapia.data.repository

import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.Camera
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.CameraStatus
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.EventSeverity
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.SecurityEvent
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SecurityRepository {

    // --- Perfil del usuario activo ---
    val currentUser = UserProfile(
        initials = "VP",
        fullName = "Víctor P.",
        roleTitle = "Guardián de Seguridad",
        email = "guardian@insercode.cl",
        assignedZone = "Guardián • Zona Sur",
        shiftTime = "14:00 - 22:00",
        totalCameras = 12,
        resolvedAlertsToday = 3
    )

    // --- Listado en memoria de Cámaras ---
    private val _cameras = MutableStateFlow(
        listOf(
            Camera("CAM-01", "Entrada Principal", "Zona Sur - Acceso 1", CameraStatus.RECORDING, timestamp = "14:32:11"),
            Camera("CAM-02", "Perímetro Norte", "Zona Sur - Sector B", CameraStatus.RECORDING, timestamp = "14:31:58"),
            Camera("CAM-03", "Bodega A", "Zona Sur - Almacén", CameraStatus.OFFLINE, timestamp = "--:--:--"),
            Camera("CAM-04", "Estacionamiento", "Zona Sur - Nivel 1", CameraStatus.RECORDING, timestamp = "14:32:05")
        )
    )
    val cameras: StateFlow<List<Camera>> = _cameras.asStateFlow()

    // --- Listado en memoria de Eventos y Alertas ---
    private val _events = MutableStateFlow(
        listOf(
            SecurityEvent(
                id = "EVT-001",
                title = "Movimiento no autorizado detectado",
                location = "Perímetro Norte - Cámara 04",
                timestamp = "14:32",
                severity = EventSeverity.CRITICAL,
                aiConfidence = "92% persona detectada",
                durationSeconds = 12,
                cameraCode = "CAM-04"
            ),
            SecurityEvent(
                id = "EVT-002",
                title = "Acceso autorizado",
                location = "Entrada Principal - Credencial M092",
                timestamp = "14:10",
                severity = EventSeverity.INFO
            ),
            SecurityEvent(
                id = "EVT-003",
                title = "Cámara desconectada",
                location = "Bodega A - Error de enlace",
                timestamp = "13:55",
                severity = EventSeverity.WARNING
            ),
            SecurityEvent(
                id = "EVT-004",
                title = "Ronda completada",
                location = "Ruta Sur completa",
                timestamp = "13:42",
                severity = EventSeverity.INFO
            )
        )
    )
    val events: StateFlow<List<SecurityEvent>> = _events.asStateFlow()

    // --- Operaciones de negocio ---
    fun resolveEvent(eventId: String) {
        _events.value = _events.value.map { event ->
            if (event.id == eventId) event.copy(isConfirmed = true) else event
        }
    }

    fun getCameraById(id: String): Camera? {
        return _cameras.value.find { it.id == id }
    }
}