package com.example.mcmovilcare.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.viewmodels.IfixitViewModel

@Composable
fun GuiasIfixit(equipo: Equipo) {
    val vm: IfixitViewModel = viewModel(key = "ifixit-${equipo.id}")
    val estado by vm.estado.collectAsState()
    val uriHandler = LocalUriHandler.current
    var errorEnlace by remember { mutableStateOf(false) }
    val consulta = "${equipo.marca} ${equipo.modelo}".trim()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Guías de reparación", style = MaterialTheme.typography.titleMedium)
            Text("Consulta referencias externas para ${equipo.marca} ${equipo.modelo}.")
            Text("Son orientativas. Confirma el modelo exacto y consulta al servicio técnico antes de intervenir el equipo.", style = MaterialTheme.typography.bodySmall)
            Button(onClick = { errorEnlace = false; vm.buscar(consulta) }, enabled = !estado.cargando) {
                Text(if (estado.cargando) "Buscando…" else "Buscar guías en iFixit")
            }

            if (estado.consultado && estado.guias.isEmpty() && estado.consulta == consulta && equipo.modelo.isNotBlank()) {
                OutlinedButton(onClick = { vm.buscar(equipo.modelo) }) { Text("Buscar solo por modelo") }
            }
            if (estado.cargando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (estado.consultado) {
                Text("Búsqueda: ${estado.consulta}", style = MaterialTheme.typography.bodySmall)
                if (estado.guias.isEmpty()) Text("No se encontraron guías. Puedes continuar con tu solicitud de atención.")
                estado.guias.forEach { guia ->
                    HorizontalDivider()
                    Text(guia.title.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                    Text("Fuente: iFixit${guia.username?.takeIf { it.isNotBlank() }?.let { " · Autor: $it" }.orEmpty()}", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = {
                        errorEnlace = runCatching { uriHandler.openUri(requireNotNull(guia.url)) }.isFailure
                    }) { Text("Abrir guía original") }
                }
            }
            if (errorEnlace) Text("No se pudo abrir el navegador.", color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { errorEnlace = runCatching { uriHandler.openUri("https://www.ifixit.com/Info/Licensing") }.isFailure }) {
                Text("iFixit · Licencia CC BY-NC-SA 3.0")
            }
        }
    }
}
