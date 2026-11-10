package com.example.dsy1105_002d_vvaldivia_jtapia.data.model

data class UserProfile(
    val initials: String,
    val fullName: String,
    val roleTitle: String,
    val email: String,
    val assignedZone: String,
    val shiftTime: String,
    val totalCameras: Int,
    val resolvedAlertsToday: Int,
    val isOnline: Boolean = true,
    val isTwoFactorEnabled: Boolean = true,
    val isPushNotificationsEnabled: Boolean = true,
    val isCriticalAlertsEnabled: Boolean = true
)