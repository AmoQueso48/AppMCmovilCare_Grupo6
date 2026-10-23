package com.example.mcmovilcare.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mcmovilcare.screens.components.BotonConLoader
import com.example.mcmovilcare.screens.components.CampoFormulario
import com.example.mcmovilcare.screens.components.SelectorChips
import com.example.mcmovilcare.viewmodels.EquiposViewModel
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.SolicitudViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaSolicitudScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel,
    equiposViewModel: EquiposViewModel,
    solicitudViewModel: SolicitudViewModel
) {
    val estado by solicitudViewModel.estado.collectAsState()
    val exito by solicitudViewModel.exito.collectAsState()
    val enviando by solicitudViewModel.enviando.collectAsState()
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val equipos by equiposViewModel.equipos.collectAsState()

    val tiposServicio = listOf("Reparacion", "Diagnostico", "Mantencion", "Consultoria")
    val prioridades = listOf("Baja", "Media", "Alta")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitud de atención") },
                navigationIcon = { TextButton(onClick = { mainViewModel.navigateBack() }) { Text("Volver") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SelectorChips(
                titulo = "Equipo",
                opciones = equipos,
                seleccionado = equipos.find { it.id == estado.equipoId },
                etiqueta = { "${it.marca} ${it.modelo}" },
                onSeleccion = { solicitudViewModel.onEquipoChange(it.id) },
                error = estado.errores.equipo
            )

            SelectorChips(
                titulo = "Tipo de servicio",
                opciones = tiposServicio,
                seleccionado = estado.tipoServicio.ifBlank { null },
                etiqueta = { it },
                onSeleccion = solicitudViewModel::onTipoServicioChange,
                error = estado.errores.tipoServicio
            )

            SelectorChips(
                titulo = "Prioridad",
                opciones = prioridades,
                seleccionado = estado.prioridad.ifBlank { null },
                etiqueta = { it },
                onSeleccion = solicitudViewModel::onPrioridadChange,
                error = estado.errores.prioridad
            )

            CampoFormulario(
                valor = estado.descripcion,
                onCambio = solicitudViewModel::onDescripcionChange,
                etiqueta = "Describe el problema",
                icono = Icons.Default.Edit,
                error = estado.errores.descripcion,
                unaLinea = false
            )
            Text("${estado.descripcion.length}/300", style = MaterialTheme.typography.bodySmall)

            BotonConLoader(
                modifier = Modifier.fillMaxWidth(),
                texto = "Enviar solicitud",
                textoCargando = "Enviando…",
                cargando = enviando,
                habilitado = !exito,
                onClick = {
                    solicitudViewModel.enviar(usuario?.correo ?: "") { mainViewModel.navigateBack() }
                }
            )

            AnimatedVisibility(visible = exito) {
                Text("Solicitud enviada!", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
