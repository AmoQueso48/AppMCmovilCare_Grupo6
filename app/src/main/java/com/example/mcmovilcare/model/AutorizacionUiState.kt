package com.example.mcmovilcare.model

data class AutorizacionUiState(
    val motivo: String = "",
    val errores: AutorizacionErrores = AutorizacionErrores()
)

data class AutorizacionErrores(
    val motivo: String? = null
)