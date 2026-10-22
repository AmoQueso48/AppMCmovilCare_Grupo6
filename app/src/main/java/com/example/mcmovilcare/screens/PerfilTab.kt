package com.example.mcmovilcare.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mcmovilcare.navigation.Screen
import com.example.mcmovilcare.screens.components.ImagenInteligente
import com.example.mcmovilcare.utils.crearUriFoto
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.PerfilViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel

@Composable
fun PerfilTab(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel,
    perfilViewModel: PerfilViewModel
) {
    val context = LocalContext.current
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val imagen by perfilViewModel.imagen.collectAsState()

    var uriTemporal by remember { mutableStateOf<Uri?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(usuario?.correo) {
        usuario?.correo?.let { perfilViewModel.cargarFoto(it) }
    }

    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) perfilViewModel.actualizarDesdeGaleria(uri)
    }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val uri = uriTemporal
        if (exito && uri != null) perfilViewModel.actualizarDesdeCamara(uri)
    }

    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            mensaje = null
            val uri = crearUriFoto(context)
            uriTemporal = uri
            camara.launch(uri)
        } else {
            mensaje = "Sin permiso de cámara no se puede tomar la foto"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagenInteligente(uri = imagen)

        Text(usuario?.nombre ?: "", style = MaterialTheme.typography.titleLarge)
        Text(usuario?.correo ?: "")

        Button(modifier = Modifier.fillMaxWidth(), onClick = { galeria.launch("image/*") }) {
            Text("Elegir de la galería")
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = { permisoCamara.launch(Manifest.permission.CAMERA) }) {
            Text("Tomar foto")
        }

        mensaje?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                perfilViewModel.limpiar()
                usuarioViewModel.cerrarSesion {
                    mainViewModel.navigateTo(Screen.Welcome, popUpTo = Screen.Home, inclusive = true)
                }
            }
        ) { Text("Cerrar sesión") }
    }
}
