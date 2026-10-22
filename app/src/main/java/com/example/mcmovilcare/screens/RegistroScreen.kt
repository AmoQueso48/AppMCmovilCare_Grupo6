package com.example.mcmovilcare.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mcmovilcare.screens.components.CampoFormulario
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(mainViewModel: MainViewModel, usuarioViewModel: UsuarioViewModel) {
    val estado by usuarioViewModel.registro.collectAsState()
    val exito by usuarioViewModel.registroExitoso.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = { TextButton(onClick = { mainViewModel.navigateBack() }) { Text("Volver") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoFormulario(
                valor = estado.nombre,
                onCambio = usuarioViewModel::onRegistroNombreChange,
                etiqueta = "Nombre",
                icono = Icons.Default.Person,
                error = estado.errores.nombre
            )
            CampoFormulario(
                valor = estado.correo,
                onCambio = usuarioViewModel::onRegistroCorreoChange,
                etiqueta = "Correo electronico",
                icono = Icons.Default.Email,
                error = estado.errores.correo,
                tipoTeclado = KeyboardType.Email
            )
            CampoFormulario(
                valor = estado.telefono,
                onCambio = usuarioViewModel::onRegistroTelefonoChange,
                etiqueta = "Teléfono",
                icono = Icons.Default.Phone,
                error = estado.errores.telefono,
                tipoTeclado = KeyboardType.Phone
            )
            CampoFormulario(
                valor = estado.clave,
                onCambio = usuarioViewModel::onRegistroClaveChange,
                etiqueta = "Clave",
                icono = Icons.Default.Lock,
                error = estado.errores.clave,
                esClave = true
            )
            CampoFormulario(
                valor = estado.repetirClave,
                onCambio = usuarioViewModel::onRegistroRepetirClaveChange,
                etiqueta = "Repetir clave",
                icono = Icons.Default.Lock,
                error = estado.errores.repetirClave,
                esClave = true
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaTerminos,
                    onCheckedChange = usuarioViewModel::onRegistroTerminosChange
                )
                Text("Acepto los terminos y condiciones")
            }
            AnimatedVisibility(visible = estado.errores.terminos != null) {
                Text(
                    estado.errores.terminos ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !exito,
                onClick = { usuarioViewModel.registrarse { mainViewModel.navigateBack() } }
            ) { Text("Registrarme") }

            AnimatedVisibility(visible = exito) {
                Text(
                    "Cuenta creada con exito caballerito! Volviendo al inicio de sesion…",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
