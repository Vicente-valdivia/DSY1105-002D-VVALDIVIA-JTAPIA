package com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.Camera
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.CameraStatus
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.EventSeverity
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.SecurityEvent
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfile
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.SecurityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val displayName: String = "Guardián",
    val initials: String = "VG",
    val zone: String = "Guardián • Zona Sur",
    val isOnline: Boolean = true,
    val cameras: List<Camera> = emptyList(),
    val activeCameras: Int = 0,
    /** Eventos WARNING/CRITICAL aún sin confirmar. */
    val pendingAlerts: Int = 0,
    /** Cámaras activas / total, entre 0 y 1. */
    val operationalRatio: Float = 0f,
    val recentEvents: List<SecurityEvent> = emptyList()
)

/**
 * Estado y acciones del Inicio. Toda la lógica vive aquí (la pantalla solo dibuja y delega),
 * de modo que conectar una fuente de datos real después solo implica cambiar el repositorio.
 */
class HomeViewModel : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        SecurityRepository.currentUserFlow,
        SecurityRepository.cameras,
        SecurityRepository.events
    ) { user, cameras, events -> buildState(user, cameras, events) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = buildState(
            SecurityRepository.currentUser,
            SecurityRepository.cameras.value,
            SecurityRepository.events.value
        )
    )

    /** Cambia el estado de disponibilidad del guardia (EN LÍNEA / FUERA DE LÍNEA). */
    fun toggleOnline() {
        SecurityRepository.setOnline(!SecurityRepository.currentUser.isOnline)
    }

    /** Marca un evento como atendido. */
    fun confirmEvent(eventId: String) {
        SecurityRepository.resolveEvent(eventId)
    }

    /**
     * Registra una incidencia reportada por el guardia.
     * @return false si el título es demasiado corto (no se registra nada).
     */
    fun reportIncident(
        title: String,
        location: String,
        severity: EventSeverity,
        cameraCode: String? = null
    ): Boolean {
        if (title.trim().length < MIN_TITLE_LENGTH) return false
        SecurityRepository.addEvent(title, location, severity, cameraCode)
        return true
    }

    companion object {
        const val MIN_TITLE_LENGTH = 3
        const val RECENT_EVENTS_LIMIT = 4

        internal fun buildState(
            user: UserProfile,
            cameras: List<Camera>,
            events: List<SecurityEvent>
        ): HomeUiState {
            val active = cameras.count { it.status != CameraStatus.OFFLINE }
            return HomeUiState(
                displayName = user.fullName.trim().substringBefore(" ").ifBlank { "Guardián" },
                initials = user.initials.ifBlank { "VG" },
                zone = user.assignedZone.ifBlank { "Guardián • Zona Sur" },
                isOnline = user.isOnline,
                cameras = cameras,
                activeCameras = active,
                pendingAlerts = events.count { !it.isConfirmed && it.severity != EventSeverity.INFO },
                operationalRatio = if (cameras.isEmpty()) 0f else active.toFloat() / cameras.size,
                recentEvents = events.take(RECENT_EVENTS_LIMIT)
            )
        }
    }
}
