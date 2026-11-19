package com.example.mcmovilcare.repository

import com.example.mcmovilcare.data.remote.AutorizacionBody
import com.example.mcmovilcare.data.remote.RetrofitInstance
import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.model.Mantencion
import com.example.mcmovilcare.model.OrdenDeTrabajo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class EquipoRepository {
    private val api = RetrofitInstance.api
    private val _ordenes = MutableStateFlow<List<OrdenDeTrabajo>>(emptyList())
    private var mantenciones: List<Mantencion> = emptyList()
    val ordenes: Flow<List<OrdenDeTrabajo>> = _ordenes
    suspend fun cargar(): Result<List<Equipo>> = runCatching {
        val equipos = api.equipos()
        _ordenes.value = api.ordenes()
        mantenciones = api.mantenciones()
        equipos
    }
    fun mantencionesDe(equipoId: Int): List<Mantencion> =
        mantenciones.filter { it.equipoId == equipoId }.sortedBy { it.fecha }
    suspend fun responderAutorizacion(
        ordenId: Int, aprobar: Boolean, comentario: String
    ): Result<OrdenDeTrabajo> = runCatching {
        val actualizada = api.responderAutorizacion(
            ordenId, AutorizacionBody(aprobar, comentario.trim().ifBlank { null })
        )
        _ordenes.value = _ordenes.value.map { if (it.id == ordenId) actualizada else it }
        actualizada
    }
}