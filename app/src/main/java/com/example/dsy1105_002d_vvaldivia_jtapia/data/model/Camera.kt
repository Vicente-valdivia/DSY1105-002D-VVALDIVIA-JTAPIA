package com.example.dsy1105_002d_vvaldivia_jtapia.data.model

enum class CameraStatus {
    ONLINE,
    OFFLINE,
    RECORDING
}

data class Camera(
    val id: String,
    val name: String,
    val locationZone: String,
    val status: CameraStatus,
    val isHd: Boolean = true,
    val timestamp: String,
    val signalStrength: String = "Excelente",
    val storageType: String = "Nube + Local"
)