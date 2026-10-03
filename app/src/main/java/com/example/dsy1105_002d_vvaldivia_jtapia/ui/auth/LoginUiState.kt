package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.User

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val user: User) : LoginUiState
    data class Error(val message: String) : LoginUiState
}