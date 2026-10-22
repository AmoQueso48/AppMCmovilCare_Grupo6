package com.example.mcmovilcare.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mcmovilcare.navigation.Screen
import com.example.mcmovilcare.utils.formatearFecha
import com.example.mcmovilcare.viewmodels.EquiposViewModel
import com.example.mcmovilcare.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEquipoScreen(mainViewModel: MainViewModel, equiposViewModel: EquiposViewModel) {
    val equipo by equiposViewModel.equipoSeleccionado.collectAsState()
    val ordenes by equiposViewModel.ordenes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del equipo") },
                navigationIcon = { TextButton(onClick = { mainViewModel.navigateBack() }) { Text("Volver") } }
            )
        }
    ) { padding ->
        equipo?.let { eq ->
            val vigente = eq.garantiaVigente()
            val colorGarantia by animateColorAsState(
                targetValue = if (vigente) Color(0xFF4CAF50) else Color(0xFFB0BEC5),
                animationSpec = tween(durationMillis = 500),
                label = "colorGarantia"
            )
            val mantenciones = remember(eq.id) { equiposViewModel.mantencionesDe(eq.id) }

            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${eq.marca} ${eq.modelo}", style = MaterialTheme.typography.headlineSmall)
                        Text("${eq.tipo} · serie ${eq.serie}")
                        Text("Accesorios: ${eq.accesorios}")
                        Text(
                            if (vigente) "Garantía vigente hasta ${formatearFecha(eq.garantiaHasta)}"
                            else "Garantía vencida el ${formatearFecha(eq.garantiaHasta)}",
                            color = colorGarantia
                        )
                    }
                }

                item { Text("Órdenes de trabajo", style = MaterialTheme.typography.titleMedium) }
                items(ordenes.filter { it.equipoId == eq.id }) { orden ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            equiposViewModel.seleccionarOrden(orden)
                            mainViewModel.navigateTo(Screen.Orden)
                        }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Orden #${orden.id} · ${orden.estado.etiqueta}")
                            Text(orden.diagnostico)
                        }
                    }
                }

                item { Text("Mantenciones", style = MaterialTheme.typography.titleMedium) }
                if (mantenciones.isEmpty()) {
                    item { Text("Sin mantenciones registradas") }
                }
                items(mantenciones) { m ->
                    val proxima = m.fecha >= System.currentTimeMillis()
                    Text("${if (proxima) "Próxima" else "Realizada"} · ${formatearFecha(m.fecha)} · ${m.tipo}")
                }
            }
        }
    }
}
