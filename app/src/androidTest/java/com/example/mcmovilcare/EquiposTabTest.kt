package com.example.mcmovilcare

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.model.Usuario
import com.example.mcmovilcare.screens.EquiposTab
import com.example.mcmovilcare.ui.theme.MCmovilCareTheme
import com.example.mcmovilcare.viewmodels.EquiposViewModel
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class EquiposTabTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun los_equipos_deben_aparecer_en_pantalla() {
        // 1. Creamos un Equipo real con garantía vigente (vence en 24 horas)
        val fakeEquipo = Equipo(
            id = 1,
            clienteCorreo = "matias@correo.cl",
            tipo = "Smartphone",
            marca = "Samsung",
            modelo = "Galaxy S24",
            serie = "SN123456",
            accesorios = "Cargador y caja",
            garantiaHasta = System.currentTimeMillis() + 86_400_000L
        )

        // 2. Simulamos los StateFlows de los ViewModels
        val fakeUsuario = Usuario("Matías", "matias@correo.cl", "+56912345678", "")
        val mockMainVm = mockk<MainViewModel>(relaxed = true)
        val mockUsuarioVm = mockk<UsuarioViewModel>(relaxed = true) {
            every { usuarioActual } returns MutableStateFlow(fakeUsuario)
        }
        val mockEquiposVm = mockk<EquiposViewModel>(relaxed = true) {
            every { equipos } returns MutableStateFlow(listOf(fakeEquipo))
            every { cargando } returns MutableStateFlow(false)
            every { error } returns MutableStateFlow(null)
        }

        // 3. Levantamos EquiposTab en el emulador
        composeTestRule.setContent {
            MCmovilCareTheme {
                EquiposTab(
                    mainViewModel = mockMainVm,
                    usuarioViewModel = mockUsuarioVm,
                    equiposViewModel = mockEquiposVm
                )
            }
        }

        // 4. Verificamos que la tarjeta del equipo se renderizó correctamente
        composeTestRule.onNodeWithText("Samsung Galaxy S24").assertIsDisplayed()
        composeTestRule.onNodeWithText("Smartphone").assertIsDisplayed()
        composeTestRule.onNodeWithText("Garantía vigente").assertIsDisplayed()
    }

    @Test
    fun debe_mostrar_mensaje_vacio_cuando_no_hay_equipos() {
        val fakeUsuario = Usuario("Matías", "matias@correo.cl", "+56912345678", "")
        val mockMainVm = mockk<MainViewModel>(relaxed = true)
        val mockUsuarioVm = mockk<UsuarioViewModel>(relaxed = true) {
            every { usuarioActual } returns MutableStateFlow(fakeUsuario)
        }
        val mockEquiposVm = mockk<EquiposViewModel>(relaxed = true) {
            every { equipos } returns MutableStateFlow(emptyList())
            every { cargando } returns MutableStateFlow(false)
            every { error } returns MutableStateFlow(null)
        }

        composeTestRule.setContent {
            MCmovilCareTheme {
                EquiposTab(
                    mainViewModel = mockMainVm,
                    usuarioViewModel = mockUsuarioVm,
                    equiposViewModel = mockEquiposVm
                )
            }
        }

        composeTestRule.onNodeWithText("Aún no tienes equipos registrados").assertIsDisplayed()
    }
}