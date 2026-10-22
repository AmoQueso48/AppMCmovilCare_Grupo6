package com.example.mcmovilcare.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mcmovilcare.navigation.Screen
import com.example.mcmovilcare.screens.components.CampoFormulario
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel

@Composable
fun LoginScreen(mainViewModel: MainViewModel, usuarioViewModel: UsuarioViewModel) {
    val estado by usuarioViewModel.login.collectAsState()
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val cargando by usuarioViewModel.cargando.collectAsState()

    LaunchedEffect(cargando) {
        if (!cargando && usuario != null) {
            mainViewModel.navigateTo(Screen.Home, popUpTo = Screen.Welcome, inclusive = true)
        }
    }

    if (cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)

        CampoFormulario(
            valor = estado.correo,
            onCambio = usuarioViewModel::onLoginCorreoChange,
            etiqueta = "Correo electrónico",
            icono = Icons.Default.Email,
            error = estado.errores.correo,
            tipoTeclado = KeyboardType.Email
        )
        CampoFormulario(
            valor = estado.clave,
            onCambio = usuarioViewModel::onLoginClaveChange,
            etiqueta = "Clave",
            icono = Icons.Default.Lock,
            error = estado.errores.clave,
            esClave = true
        )

        AnimatedVisibility(visible = estado.errorGeneral != null) {
            Text(estado.errorGeneral ?: "", color = MaterialTheme.colorScheme.error)
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                usuarioViewModel.iniciarSesion {
                    mainViewModel.navigateTo(Screen.Home, popUpTo = Screen.Welcome, inclusive = true)
                }
            }
        ) { Text("Ingresar") }
        TextButton(onClick = { mainViewModel.navigateTo(Screen.Registro) }) { Text("Crear cuenta") }
    }
}