package com.example.mcmovilcare.model

data class SolicitudUiState(
    val equipoId: Int? = null,
    val tipoServicio: String = "",
    val prioridad: String = "",
    val descripcion: String = "",
    val errores: SolicitudErrores = SolicitudErrores()
)

data class SolicitudErrores(
    val equipo: String? = null,
    val tipoServicio: String? = null,
    val prioridad: String? = null,
    val descripcion: String? = null
)
