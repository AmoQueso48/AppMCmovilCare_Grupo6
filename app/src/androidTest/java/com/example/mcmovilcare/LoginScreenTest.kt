package com.example.mcmovilcare

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.mcmovilcare.model.LoginUiState
import com.example.mcmovilcare.navigation.Screen
import com.example.mcmovilcare.screens.LoginScreen
import com.example.mcmovilcare.ui.theme.MCmovilCareTheme
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun debe_mostrar_formulario_de_login_y_permitir_escribir_y_presionar_ingresar() {
        // 1. Simulamos los StateFlows en estado inicial listo para interactuar
        val loginStateFlow = MutableStateFlow(LoginUiState())
        val mockMainVm = mockk<MainViewModel>(relaxed = true)
        val mockUsuarioVm = mockk<UsuarioViewModel>(relaxed = true) {
            every { login } returns loginStateFlow
            every { usuarioActual } returns MutableStateFlow(null)
            every { cargando } returns MutableStateFlow(false)
            every { onLoginCorreoChange(any()) } answers {
                loginStateFlow.value = loginStateFlow.value.copy(correo = firstArg())
            }
            every { onLoginClaveChange(any()) } answers {
                loginStateFlow.value = loginStateFlow.value.copy(clave = firstArg())
            }
        }

        // 2. Levantamos LoginScreen en el emulador
        composeTestRule.setContent {
            MCmovilCareTheme {
                LoginScreen(
                    mainViewModel = mockMainVm,
                    usuarioViewModel = mockUsuarioVm
                )
            }
        }

        // 3. Verificamos que todos los elementos principales estén visibles
        composeTestRule.onNodeWithText("Iniciar sesión").assertIsDisplayed()
        composeTestRule.onNodeWithText("Correo electrónico").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clave").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ingresar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Crear cuenta").assertIsDisplayed()

        // 4. Simulamos que el usuario escribe sus credenciales y presiona "Ingresar"
        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("matias@correo.cl")
        composeTestRule.onNodeWithText("Clave").performTextInput("clave123")
        composeTestRule.onNodeWithText("Ingresar").performClick()

        // 5. Verificamos que la UI invocó iniciarSesion en el ViewModel
        verify { mockUsuarioVm.iniciarSesion(any()) }
    }

    @Test
    fun presionar_crear_cuenta_debe_navegar_a_registro() {
        val mockMainVm = mockk<MainViewModel>(relaxed = true)
        val mockUsuarioVm = mockk<UsuarioViewModel>(relaxed = true) {
            every { login } returns MutableStateFlow(LoginUiState())
            every { usuarioActual } returns MutableStateFlow(null)
            every { cargando } returns MutableStateFlow(false)
        }

        composeTestRule.setContent {
            MCmovilCareTheme {
                LoginScreen(
                    mainViewModel = mockMainVm,
                    usuarioViewModel = mockUsuarioVm
                )
            }
        }

        composeTestRule.onNodeWithText("Crear cuenta").performClick()
        verify { mockMainVm.navigateTo(Screen.Registro) }
    }
}