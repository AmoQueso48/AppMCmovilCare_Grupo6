package com.example.mcmovilcare.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.mcmovilcare.viewmodels.EquiposViewModel
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.PerfilViewModel
import com.example.mcmovilcare.viewmodels.SolicitudViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel,
    equiposViewModel: EquiposViewModel,
    solicitudViewModel: SolicitudViewModel,
    perfilViewModel: PerfilViewModel
) {
    var pestana by rememberSaveable { mutableStateOf(0) }

    val destinos = listOf(
        "Equipos" to Icons.Default.Build,
        "Solicitudes" to Icons.Default.Email,
        "Perfil" to Icons.Default.Person
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(destinos[pestana].first) }) },
        bottomBar = {
            NavigationBar {
                destinos.forEachIndexed { indice, (titulo, icono) ->
                    NavigationBarItem(
                        selected = pestana == indice,
                        onClick = { pestana = indice },
                        icon = { Icon(icono, contentDescription = titulo) },
                        label = { Text(titulo) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Crossfade(targetState = pestana, label = "pestanas") { destino ->
                when (destino) {
                    0 -> EquiposTab(mainViewModel, usuarioViewModel, equiposViewModel)
                    1 -> SolicitudesTab(mainViewModel, usuarioViewModel, solicitudViewModel)
                    else -> PerfilTab(mainViewModel, usuarioViewModel, perfilViewModel)
                }
            }
        }
    }
}
