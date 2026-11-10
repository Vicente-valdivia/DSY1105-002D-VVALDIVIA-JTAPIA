package com.example.dsy1105_002d_vvaldivia_jtapia.data.model

enum class EventSeverity {
    INFO,
    WARNING,
    CRITICAL
}

data class SecurityEvent(
    val id: String,
    val title: String,
    val location: String,
    val timestamp: String,
    val severity: EventSeverity,
    val isConfirmed: Boolean = false,
    val aiConfidence: String? = null,
    val durationSeconds: Int? = null,
    val cameraCode: String? = null
)