package com.example.mcmovilcare.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> SelectorChips(
    titulo: String,
    opciones: List<T>,
    seleccionado: T?,
    etiqueta: (T) -> String,
    onSeleccion: (T) -> Unit,
    error: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opciones.forEach { opcion ->
                FilterChip(
                    selected = opcion == seleccionado,
                    onClick = { onSeleccion(opcion) },
                    label = { Text(etiqueta(opcion)) }
                )
            }
        }
        AnimatedVisibility(visible = error != null) {
            Text(error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}
