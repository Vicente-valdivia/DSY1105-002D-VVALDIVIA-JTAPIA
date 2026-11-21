package com.example.dsy1105_002d_vvaldivia_jtapia.data.repository

import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.Camera
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.CameraStatus
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.EventSeverity
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.SecurityEvent
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object SecurityRepository {

    // --- Perfil del usuario activo (observable: las pantallas se actualizan solas) ---
    private val _currentUser = MutableStateFlow(UserProfile())
    val currentUserFlow: StateFlow<UserProfile> = _currentUser.asStateFlow()

    var currentUser: UserProfile
        get() = _currentUser.value
        set(value) {
            _currentUser.value = value
        }

    fun setOnline(online: Boolean) {
        _currentUser.update { it.copy(isOnline = online) }
    }

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
                location = "Perímetro Norte - Cámara 02",
                timestamp = "14:32",
                severity = EventSeverity.CRITICAL,
                aiConfidence = "92% persona detectada",
                durationSeconds = 12,
                cameraCode = "CAM-02"
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
                severity = EventSeverity.WARNING,
                cameraCode = "CAM-03"
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
        // update{} es atómico: evita perder cambios si dos coroutines escriben a la vez
        _events.update { list ->
            list.map { event -> if (event.id == eventId) event.copy(isConfirmed = true) else event }
        }
    }

    /** Registra una incidencia nueva (queda al inicio de la lista). */
    fun addEvent(
        title: String,
        location: String,
        severity: EventSeverity,
        cameraCode: String? = null
    ): SecurityEvent {
        var created: SecurityEvent? = null
        _events.update { current ->
            val nextNumber = (current.mapNotNull { it.id.removePrefix("EVT-").toIntOrNull() }.maxOrNull() ?: 0) + 1
            val event = SecurityEvent(
                id = String.format(Locale.ROOT, "EVT-%03d", nextNumber),
                title = title.trim(),
                location = location.trim().ifBlank { "Sin ubicación indicada" },
                timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)),
                severity = severity,
                cameraCode = cameraCode
            )
            created = event
            listOf(event) + current
        }
        return created!!
    }

    fun getCameraById(id: String): Camera? {
        return _cameras.value.find { it.id == id }
    }
}
