package com.example.mcmovilcare.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcmovilcare.data.remote.IfixitGuia
import com.example.mcmovilcare.repository.IfixitRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class IfixitViewModel : ViewModel() {
    private val repo = IfixitRepository()
    private val _estado = MutableStateFlow(IfixitUiState())
    val estado: StateFlow<IfixitUiState> = _estado

    fun buscar(consulta: String) {
        if (consulta.isBlank() || _estado.value.cargando) return
        _estado.value = IfixitUiState(consulta = consulta, cargando = true)
        viewModelScope.launch {
            try {
                val guias = repo.buscarGuias(consulta)
                _estado.value = IfixitUiState(consulta = consulta, guias = guias, consultado = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val mensaje = when ((e as? HttpException)?.code()) {
                    401, 403 -> "iFixit requiere autorización o una clave de API."
                    429 -> "iFixit alcanzó su límite de consultas. Inténtalo más tarde."
                    else -> "No se pudieron consultar las guías de iFixit. Revisa Internet e inténtalo de nuevo."
                }
                _estado.value = IfixitUiState(consulta = consulta, error = mensaje)
            }
        }
    }
}

data class IfixitUiState(
    val consulta: String = "",
    val cargando: Boolean = false,
    val consultado: Boolean = false,
    val guias: List<IfixitGuia> = emptyList(),
    val error: String? = null
)
