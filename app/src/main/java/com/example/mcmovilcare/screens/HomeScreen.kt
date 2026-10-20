package com.example.mcmovilcare.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mcmovilcare.navigation.Screen
import com.example.mcmovilcare.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MC Movil Care - Menú Principal") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.navigateTo(Screen.Detail) }
            ) {
                Text(text = "Ver Detalles del Equipo (Mantenimiento)")
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.navigateTo(Screen.Form) }
            ) {
                Text(text = "Solicitud de Atención (Formulario)")
            }
        }
    }
}