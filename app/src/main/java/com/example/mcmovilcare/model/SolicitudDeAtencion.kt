package com.example.mcmovilcare.model

data class SolicitudDeAtencion(
    val id: Int,
    val clienteCorreo: String,
    val equipoId: Int?,
    val tipoServicio: String,
    val prioridad: String,
    val descripcion: String,
    val estado: EstadoSolicitud = EstadoSolicitud.ENVIADA,
    val fechaCreacion: Long = System.currentTimeMillis()
)
