package com.example.mcmovilcare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcmovilcare.model.SolicitudDeAtencion
import com.example.mcmovilcare.model.SolicitudErrores
import com.example.mcmovilcare.model.SolicitudUiState
import com.example.mcmovilcare.repository.SolicitudRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// PASO 3 · viewmodels/  — formulario "Nueva solicitud de atención".
class SolicitudViewModel : ViewModel() {

    private val repo = SolicitudRepository()

    private val _estado = MutableStateFlow(SolicitudUiState())
    val estado: StateFlow<SolicitudUiState> = _estado

    private val _exito = MutableStateFlow(false)      // alimenta el AnimatedVisibility de éxito
    val exito: StateFlow<Boolean> = _exito

    fun solicitudesDe(correo: String): Flow<List<SolicitudDeAtencion>> = repo.solicitudesDe(correo)

    fun cancelar(id: Int) { repo.cancelar(id) }

    fun onEquipoChange(id: Int) {
        _estado.update { it.copy(equipoId = id, errores = it.errores.copy(equipo = null)) }
    }
    fun onTipoServicioChange(valor: String) {
        _estado.update { it.copy(tipoServicio = valor, errores = it.errores.copy(tipoServicio = null)) }
    }
    fun onPrioridadChange(valor: String) {
        _estado.update { it.copy(prioridad = valor, errores = it.errores.copy(prioridad = null)) }
    }
    fun onDescripcionChange(valor: String) {
        _estado.update { it.copy(descripcion = valor, errores = it.errores.copy(descripcion = null)) }
    }

    fun validarFormulario(): Boolean {
        val e = _estado.value
        val errores = SolicitudErrores(
            equipo = if (e.equipoId == null) "Selecciona un equipo" else null,
            tipoServicio = if (e.tipoServicio.isBlank()) "Selecciona el tipo de servicio" else null,
            prioridad = if (e.prioridad.isBlank()) "Selecciona la prioridad" else null,
            descripcion = when {
                e.descripcion.isBlank() -> "La descripción es obligatoria"
                e.descripcion.trim().length < 10 -> "Describe el problema con al menos 10 caracteres"
                e.descripcion.length > 300 -> "Máximo 300 caracteres (llevas ${e.descripcion.length})"
                else -> null
            }
        )
        val hayErrores = listOfNotNull(errores.equipo, errores.tipoServicio, errores.prioridad, errores.descripcion).isNotEmpty()
        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }

    fun enviar(correoCliente: String, onExito: () -> Unit) {
        if (_exito.value) return
        if (!validarFormulario()) return
        val e = _estado.value
        repo.crear(
            SolicitudDeAtencion(
                id = 0,
                clienteCorreo = correoCliente,
                equipoId = e.equipoId,
                tipoServicio = e.tipoServicio,
                prioridad = e.prioridad,
                descripcion = e.descripcion.trim()
            )
        )
        _estado.value = SolicitudUiState()
        viewModelScope.launch {
            _exito.value = true
            delay(1200)
            _exito.value = false
            onExito()
        }
    }
}
