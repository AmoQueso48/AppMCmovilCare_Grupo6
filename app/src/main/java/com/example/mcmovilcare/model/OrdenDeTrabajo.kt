package com.example.mcmovilcare.model

data class OrdenDeTrabajo(
    val id: Int,
    val equipoId: Int,
    val estado: EstadoOrden,
    val diagnostico: String,
    val cotizacionMonto: Int,
    val cotizacionDetalle: String,
    val autorizacion: EstadoAutorizacion,
    val comentarioAutorizacion: String? = null
)