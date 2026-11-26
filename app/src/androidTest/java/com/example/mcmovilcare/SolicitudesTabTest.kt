package com.example.mcmovilcare

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.mcmovilcare.model.EstadoSolicitud
import com.example.mcmovilcare.model.SolicitudDeAtencion
import com.example.mcmovilcare.model.Usuario // Ajusta el nombre si tu modelo de usuario se llama distinto (ej. Cliente)
import com.example.mcmovilcare.screens.SolicitudesTab
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.SolicitudViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SolicitudesTabTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun las_solicitudes_deben_aparecer_en_pantalla() {
        // 1. Datos simulados que deben aparecer en pantalla
        val fakeSolicitudes = listOf(
            SolicitudDeAtencion(
                id = 1,
                clienteCorreo = "cliente1@correo.cl",
                equipoId = 101,
                tipoServicio = "Mantenimiento Preventivo",
                prioridad = "Alta",
                descripcion = "Equipo no enciende",
                estado = EstadoSolicitud.ENVIADA,
                fechaCreacion = 1700000000000L
            ),
            SolicitudDeAtencion(
                id = 2,
                clienteCorreo = "cliente1@correo.cl",
                equipoId = null,
                tipoServicio = "Soporte Técnico",
                prioridad = "Media",
                descripcion = "Configuración de red",
                estado = EstadoSolicitud.ENVIADA,
                fechaCreacion = 1700000000000L
            )
        )

        // 2. Simulamos UsuarioViewModel entregando un usuario con correo válido
        val mockUsuario = mockk<Usuario>(relaxed = true) {
            every { correo } returns "cliente1@correo.cl"
        }
        val mockUsuarioVM = mockk<UsuarioViewModel>(relaxed = true) {
            every { usuarioActual } returns MutableStateFlow(mockUsuario)
        }

        // 3. Simulamos SolicitudViewModel entregando los StateFlows y el Flow de solicitudes
        val mockSolicitudVM = mockk<SolicitudViewModel>(relaxed = true) {
            every { error } returns MutableStateFlow(null)
            every { solicitudesDe("cliente1@correo.cl") } returns flowOf(fakeSolicitudes)
        }

        // 4. Simulamos MainViewModel
        val mockMainVM = mockk<MainViewModel>(relaxed = true)

        // 5. Renderizamos la pantalla real con los ViewModels simulados
        composeRule.setContent {
            SolicitudesTab(
                mainViewModel = mockMainVM,
                usuarioViewModel = mockUsuarioVM,
                solicitudViewModel = mockSolicitudVM
            )
        }

        // 6. Validamos que los textos exactos de las tarjetas y el botón se muestren en pantalla
        composeRule.onNodeWithText("Nueva solicitud de atención").assertIsDisplayed()
        composeRule.onNodeWithText("Mantenimiento Preventivo").assertIsDisplayed()
        composeRule.onNodeWithText("Equipo no enciende").assertIsDisplayed()
        composeRule.onNodeWithText("Soporte Técnico").assertIsDisplayed()
        composeRule.onNodeWithText("Configuración de red").assertIsDisplayed()
    }
}