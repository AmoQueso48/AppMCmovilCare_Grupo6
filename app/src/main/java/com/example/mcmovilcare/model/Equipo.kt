package com.example.mcmovilcare.model

data class Equipo(
    val id: Int,
    val clienteCorreo: String,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val serie: String,
    val accesorios: String,
    val garantiaHasta: Long
) {
    fun garantiaVigente(ahora: Long = System.currentTimeMillis()): Boolean = garantiaHasta >= ahora
}
