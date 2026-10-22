package com.example.mcmovilcare.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mcmovilcare.model.EstadoAutorizacion
import com.example.mcmovilcare.model.EstadoOrden
import com.example.mcmovilcare.screens.components.CampoFormulario
import com.example.mcmovilcare.viewmodels.EquiposViewModel
import com.example.mcmovilcare.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdenScreen(mainViewModel: MainViewModel, equiposViewModel: EquiposViewModel) {
    val ordenes by equiposViewModel.ordenes.collectAsState()
    val ordenId by equiposViewModel.ordenSeleccionadaId.collectAsState()
    val autorizacion by equiposViewModel.autorizacion.collectAsState()
    val exito by equiposViewModel.mensajeExito.collectAsState()
    var rechazando by rememberSaveable { mutableStateOf(false) }

    val orden = ordenes.find { it.id == ordenId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orden de trabajo") },
                navigationIcon = { TextButton(onClick = { mainViewModel.navigateBack() }) { Text("Volver") } }
            )
        }
    ) { padding ->
        if (orden != null) {
            val progreso by animateFloatAsState(
                targetValue = orden.estado.progreso,
                animationSpec = tween(durationMillis = 800),
                label = "progresoOrden"
            )

            Column(
                modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(orden.estado.etiqueta, style = MaterialTheme.typography.headlineSmall)
                LinearProgressIndicator(progress = { progreso }, modifier = Modifier.fillMaxWidth())

                Text("Diagnóstico: ${orden.diagnostico}")
                Text("Cotización: \$${orden.cotizacionMonto} · ${orden.cotizacionDetalle}")
                Text("Autorización: ${orden.autorizacion.etiqueta}")
                orden.comentarioAutorizacion?.let { Text("Comentario: $it") }

                val puedeResponder = orden.estado == EstadoOrden.COTIZADO &&
                        orden.autorizacion == EstadoAutorizacion.PENDIENTE

                if (puedeResponder) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { equiposViewModel.aprobarCotizacion(orden.id) }) { Text("Aprobar") }
                        OutlinedButton(onClick = { rechazando = true }) { Text("Rechazar") }
                    }

                    AnimatedVisibility(visible = rechazando) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CampoFormulario(
                                valor = autorizacion.motivo,
                                onCambio = equiposViewModel::onMotivoChange,
                                etiqueta = "Motivo del rechazo",
                                icono = Icons.Default.Edit,
                                error = autorizacion.errores.motivo,
                                unaLinea = false
                            )
                            Button(onClick = { equiposViewModel.rechazarCotizacion(orden.id) }) {
                                Text("Confirmar rechazo")
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = exito) {
                    Text("Respuesta enviada!", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
