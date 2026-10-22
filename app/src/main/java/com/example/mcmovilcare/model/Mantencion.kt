package com.example.mcmovilcare.model

data class Mantencion(
    val id: Int,
    val equipoId: Int,
    val fecha: Long,
    val tipo: String
)
