package com.example.mcmovilcare.repository
import com.example.mcmovilcare.model.EstadoSolicitud
import com.example.mcmovilcare.model.SolicitudDeAtencion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SolicitudRepository {

    private val _solicitudes = MutableStateFlow<List<SolicitudDeAtencion>>(emptyList())
    private var siguienteId = 1

    fun solicitudesDe(correo: String): Flow<List<SolicitudDeAtencion>> =
        _solicitudes.map { lista -> lista.filter { it.clienteCorreo == correo }.sortedByDescending { it.fechaCreacion } }

    fun crear(solicitud: SolicitudDeAtencion) {
        val nueva = solicitud.copy(id = siguienteId++, estado = EstadoSolicitud.ENVIADA, fechaCreacion = System.currentTimeMillis())
        _solicitudes.update { it + nueva }
    }

    fun cancelar(id: Int): Boolean {
        val s = _solicitudes.value.find { it.id == id } ?: return false
        if (s.estado != EstadoSolicitud.ENVIADA) return false
        _solicitudes.update { lista -> lista.map { if (it.id == id) it.copy(estado = EstadoSolicitud.CANCELADA) else it } }
        return true
    }
}
