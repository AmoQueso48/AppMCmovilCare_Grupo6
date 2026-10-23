package com.example.mcmovilcare.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mcmovilcare.model.EstadoSolicitud
import com.example.mcmovilcare.navigation.Screen
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.SolicitudViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel

@Composable
fun SolicitudesTab(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel,
    solicitudViewModel: SolicitudViewModel
) {
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val correo = usuario?.correo ?: ""

    val solicitudes by remember(correo) { solicitudViewModel.solicitudesDe(correo) }
        .collectAsState(initial = null)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { mainViewModel.navigateTo(Screen.NuevaSolicitud) }
        ) { Text("Nueva solicitud de atención") }

        val lista = solicitudes
        when {
            lista == null -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            lista.isEmpty() -> Text("Todavia no has enviado solicitudes :c")
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(lista, key = { it.id }) { s ->
                        Card(modifier = Modifier.fillMaxWidth().animateItem()) {
                            Column(
                                modifier = Modifier.padding(16.dp).animateContentSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(s.tipoServicio, style = MaterialTheme.typography.titleMedium)
                                Text("${s.estado.etiqueta} · prioridad ${s.prioridad}")
                                Text(s.descripcion)
                                if (s.estado == EstadoSolicitud.ENVIADA) {
                                    TextButton(onClick = { solicitudViewModel.cancelar(s.id) }) { Text("Cancelar solicitud") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
