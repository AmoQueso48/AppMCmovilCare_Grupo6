package com.example.mcmovilcare.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mcmovilcare.navigation.Screen
import com.example.mcmovilcare.viewmodels.EquiposViewModel
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel

@Composable
fun EquiposTab(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel,
    equiposViewModel: EquiposViewModel
) {
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val equipos by equiposViewModel.equipos.collectAsState()
    val cargando by equiposViewModel.cargando.collectAsState()

    LaunchedEffect(usuario?.correo) {
        usuario?.correo?.let { equiposViewModel.cargarEquipos(it) }
    }

    if (cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (equipos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aún no tienes equipos registrados")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(equipos) { equipo ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        equiposViewModel.seleccionarEquipo(equipo)
                        mainViewModel.navigateTo(Screen.DetalleEquipo)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${equipo.marca} ${equipo.modelo}", style = MaterialTheme.typography.titleMedium)
                            Text(equipo.tipo)
                            Text(if (equipo.garantiaVigente()) "Garantía vigente" else "Garantía vencida")
                        }
                    }
                }
            }
        }
    }
}
