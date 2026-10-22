package com.example.mcmovilcare.model

//cambio de planes de uso de room para prueba mas local sin uso de room
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
