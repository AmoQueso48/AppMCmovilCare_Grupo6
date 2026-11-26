package com.example.mcmovilcare.repository

import com.example.mcmovilcare.data.remote.ApiService
import com.example.mcmovilcare.data.remote.EstadoBody
import com.example.mcmovilcare.data.remote.RetrofitInstance
import com.example.mcmovilcare.data.remote.SolicitudBody
import com.example.mcmovilcare.model.SolicitudDeAtencion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

open class SolicitudRepository(
    private val api: ApiService = RetrofitInstance.api
) {
    private val _solicitudes = MutableStateFlow<List<SolicitudDeAtencion>>(emptyList())

    fun solicitudesDe(correo: String): Flow<List<SolicitudDeAtencion>> =
        _solicitudes.map { lista ->
            lista.filter { it.clienteCorreo.equals(correo, ignoreCase = true) }
                .sortedByDescending { it.fechaCreacion }
        }

    suspend fun refrescar(): Result<Unit> = runCatching {
        _solicitudes.value = api.solicitudes()
    }

    suspend fun crear(s: SolicitudDeAtencion): Result<SolicitudDeAtencion> = runCatching {
        val creada = api.crearSolicitud(
            SolicitudBody(requireNotNull(s.equipoId), s.tipoServicio, s.prioridad, s.descripcion)
        )
        _solicitudes.value = _solicitudes.value + creada
        creada
    }

    suspend fun editar(id: Int, cuerpo: SolicitudBody): Result<SolicitudDeAtencion> = runCatching {
        val editada = api.editarSolicitud(id, cuerpo)
        _solicitudes.value = _solicitudes.value.map { if (it.id == id) editada else it }
        editada
    }

    suspend fun cancelar(id: Int): Result<Unit> = runCatching {
        val actualizada = api.cambiarEstadoSolicitud(id, EstadoBody("CANCELADA"))
        _solicitudes.value = _solicitudes.value.map { if (it.id == id) actualizada else it }
    }

    suspend fun eliminar(id: Int): Result<Unit> = runCatching {
        val respuesta = api.eliminarSolicitud(id)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
        _solicitudes.value = _solicitudes.value.filterNot { it.id == id }
    }
}