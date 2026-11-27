package com.example.mcmovilcare.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcmovilcare.data.remote.mensajeApi
import com.example.mcmovilcare.model.AutorizacionErrores
import com.example.mcmovilcare.model.AutorizacionUiState
import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.model.Mantencion
import com.example.mcmovilcare.model.OrdenDeTrabajo
import com.example.mcmovilcare.repository.EquipoRepository
import com.example.mcmovilcare.utils.mostrarNotificacion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EquiposViewModel @JvmOverloads constructor(
    application: Application,
    private val repo: EquipoRepository = EquipoRepository()
) : AndroidViewModel(application) {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _equipos = MutableStateFlow<List<Equipo>>(emptyList())
    val equipos: StateFlow<List<Equipo>> = _equipos

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    fun cargarEquipos(correo: String) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            repo.cargar()
                .onSuccess { _equipos.value = it }
                .onFailure { _error.value = it.mensajeApi() }
            _cargando.value = false
        }
    }

    private val _equipoSeleccionado = MutableStateFlow<Equipo?>(null)
    val equipoSeleccionado: StateFlow<Equipo?> = _equipoSeleccionado

    private val _ordenSeleccionadaId = MutableStateFlow<Int?>(null)
    val ordenSeleccionadaId: StateFlow<Int?> = _ordenSeleccionadaId

    fun seleccionarEquipo(equipo: Equipo) {
        _equipoSeleccionado.value = equipo
    }

    fun seleccionarOrden(orden: OrdenDeTrabajo) {
        _ordenSeleccionadaId.value = orden.id
        _autorizacion.value = AutorizacionUiState()
    }

    val ordenes: StateFlow<List<OrdenDeTrabajo>> =
        repo.ordenes.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun mantencionesDe(equipoId: Int): List<Mantencion> = repo.mantencionesDe(equipoId)

    private val _autorizacion = MutableStateFlow(AutorizacionUiState())
    val autorizacion: StateFlow<AutorizacionUiState> = _autorizacion

    private val _mensajeExito = MutableStateFlow(false)
    val mensajeExito: StateFlow<Boolean> = _mensajeExito

    fun onMotivoChange(valor: String) {
        _autorizacion.update { it.copy(motivo = valor, errores = it.errores.copy(motivo = null)) }
    }

    fun validarMotivo(): Boolean {
        val motivo = _autorizacion.value.motivo
        val errores = AutorizacionErrores(
            motivo = when {
                motivo.isBlank() -> "Indica el motivo del rechazo"
                motivo.trim().length < 10 -> "El motivo debe tener al menos 10 caracteres"
                else -> null
            }
        )
        _autorizacion.update { it.copy(errores = errores) }
        return errores.motivo == null
    }

    fun aprobarCotizacion(ordenId: Int) {
        viewModelScope.launch {
            repo.responderAutorizacion(ordenId, aprobar = true, comentario = "")
                .onSuccess {
                    mostrarExito()
                    mostrarNotificacion(
                        getApplication<Application>(),
                        "Cotización aprobada",
                        "La orden #$ordenId quedó autorizada"
                    )
                }
                .onFailure { _error.value = it.mensajeApi() }
        }
    }

    fun rechazarCotizacion(ordenId: Int) {
        if (!validarMotivo()) return
        val motivo = _autorizacion.value.motivo
        viewModelScope.launch {
            repo.responderAutorizacion(ordenId, aprobar = false, comentario = motivo)
                .onSuccess {
                    mostrarExito()
                    mostrarNotificacion(
                        getApplication<Application>(),
                        "Cotización rechazada",
                        "Enviamos tu motivo para la orden #$ordenId"
                    )
                }
                .onFailure { _error.value = it.mensajeApi() }
        }
    }

    private fun mostrarExito() {
        viewModelScope.launch {
            _mensajeExito.value = true
            delay(2500)
            _mensajeExito.value = false
        }
    }
}