package com.example.mcmovilcare.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcmovilcare.model.AutorizacionErrores
import com.example.mcmovilcare.model.AutorizacionUiState
import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.model.Mantencion
import com.example.mcmovilcare.model.OrdenDeTrabajo
import com.example.mcmovilcare.repository.EquipoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EquiposViewModel : ViewModel() {
    private val repo = EquipoRepository()

    private val _equipos = MutableStateFlow<List<Equipo>>(emptyList())
    val equipos: StateFlow<List<Equipo>> = _equipos

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    fun cargarEquipos(correo: String) {
        viewModelScope.launch {
            _cargando.value = true
            delay(1000)
            _equipos.value = repo.equiposDe(correo)
            _cargando.value = false
        }
    }

    private val _equipoSeleccionado = MutableStateFlow<Equipo?>(null)
    val equipoSeleccionado: StateFlow<Equipo?> = _equipoSeleccionado

    private val _ordenSeleccionadaId = MutableStateFlow<Int?>(null)
    val ordenSeleccionadaId: StateFlow<Int?> = _ordenSeleccionadaId

    fun seleccionarEquipo(equipo: Equipo) { _equipoSeleccionado.value = equipo }

    fun seleccionarOrden(orden: OrdenDeTrabajo) {
        _ordenSeleccionadaId.value = orden.id
        _autorizacion.value = AutorizacionUiState()
    }

    val ordenes: StateFlow<List<OrdenDeTrabajo>> = repo.ordenes

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
        if (repo.responderAutorizacion(ordenId, aprobar = true, comentario = "")) mostrarExito()
    }

    fun rechazarCotizacion(ordenId: Int) {
        if (!validarMotivo()) return
        if (repo.responderAutorizacion(ordenId, aprobar = false, comentario = _autorizacion.value.motivo)) mostrarExito()
    }

    private fun mostrarExito() {
        viewModelScope.launch {
            _mensajeExito.value = true
            delay(2500)
            _mensajeExito.value = false
        }
    }
}
