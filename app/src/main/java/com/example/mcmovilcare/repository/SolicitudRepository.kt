package com.example.mcmovilcare.repository

import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.model.EstadoSolicitud
import com.example.mcmovilcare.model.SolicitudDeAtencion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SolicitudRepository(private val prefs: PreferenciasDataStore) {

    fun solicitudesDe(correo: String): Flow<List<SolicitudDeAtencion>> =
        prefs.observarSolicitudes().map { lista ->
            lista
                .filter { it.clienteCorreo.equals(correo, ignoreCase = true) }
                .sortedByDescending { it.fechaCreacion }
        }

    suspend fun crear(solicitud: SolicitudDeAtencion) {
        val actuales = prefs.observarSolicitudes().first()
        val nueva = solicitud.copy(
            id = (actuales.maxOfOrNull { it.id } ?: 0) + 1,
            estado = EstadoSolicitud.ENVIADA,
            fechaCreacion = System.currentTimeMillis()
        )
        prefs.guardarSolicitudes(actuales + nueva)
    }

    suspend fun cancelar(id: Int): Boolean {
        val actuales = prefs.observarSolicitudes().first()
        val s = actuales.find { it.id == id } ?: return false
        if (s.estado != EstadoSolicitud.ENVIADA) return false
        prefs.guardarSolicitudes(
            actuales.map { if (it.id == id) it.copy(estado = EstadoSolicitud.CANCELADA) else it }
        )
        return true
    }
}
