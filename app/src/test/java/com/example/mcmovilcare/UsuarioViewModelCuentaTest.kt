package com.example.mcmovilcare

import android.app.Application
import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.data.remote.ApiService
import com.example.mcmovilcare.data.remote.LoginBody
import com.example.mcmovilcare.data.remote.LoginRespuesta
import com.example.mcmovilcare.data.remote.RegistroBody
import com.example.mcmovilcare.data.remote.SesionApi
import com.example.mcmovilcare.data.remote.UsuarioDto
import com.example.mcmovilcare.model.RegistroUiState
import com.example.mcmovilcare.model.Usuario
import com.example.mcmovilcare.repository.UsuarioRepository
import com.example.mcmovilcare.viewmodels.UsuarioViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import java.io.IOException

private const val MENSAJE_SIN_CONEXION = "No se pudo conectar con el servidor. ¿Está encendida la API?"

private class EscenarioCuenta {
    val prefs = mockk<PreferenciasDataStore>(relaxed = true).also {
        every { it.obtenerSesion() } returns flowOf<String?>(null)
    }
    val api = mockk<ApiService>()
    val viewModel = UsuarioViewModel(
        application = mockk<Application>(relaxed = true),
        repo = UsuarioRepository(prefs = prefs, api = api)
    )

    fun llenarLogin() {
        viewModel.onLoginCorreoChange("matias@correo.cl")
        viewModel.onLoginClaveChange("clave123")
    }

    fun llenarRegistroValido() {
        viewModel.onRegistroNombreChange("Matías Pérez")
        viewModel.onRegistroCorreoChange("Matias@Correo.cl")
        viewModel.onRegistroTelefonoChange("+56912345678")
        viewModel.onRegistroClaveChange("clave123")
        viewModel.onRegistroRepetirClaveChange("clave123")
        viewModel.onRegistroTerminosChange(true)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelCuentaTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        SesionApi.token = null
    }

    afterTest {
        Dispatchers.resetMain()
        SesionApi.token = null
    }

    "iniciarSesion() debe mostrar el mensaje de conexión y no guardar sesión cuando la API no responde" {
        val e = EscenarioCuenta()
        coEvery { e.api.login(any()) } throws IOException("sin red")
        e.llenarLogin()
        var exito = false

        runTest {
            e.viewModel.iniciarSesion { exito = true }

            e.viewModel.login.value.cargando shouldBe false
            e.viewModel.login.value.errorGeneral shouldBe MENSAJE_SIN_CONEXION
            e.viewModel.usuarioActual.value shouldBe null
            exito shouldBe false
            coVerify(exactly = 0) { e.prefs.guardarSesion(any()) }
        }
    }

    "iniciarSesion() no debe llamar a la API cuando el formulario es inválido" {
        val e = EscenarioCuenta()
        var exito = false

        runTest {
            e.viewModel.iniciarSesion { exito = true }

            exito shouldBe false
            e.viewModel.login.value.errores.correo shouldBe "Campo obligatorio"
            coVerify(exactly = 0) { e.api.login(any()) }
        }
    }

    "cerrarSesion() debe limpiar el usuario actual, el token y la sesión guardada" {
        val e = EscenarioCuenta()
        val respuesta = LoginRespuesta(
            token = "token-abc",
            usuario = UsuarioDto("Matías", "matias@correo.cl", "+56912345678", "cliente")
        )
        coEvery { e.api.login(LoginBody("matias@correo.cl", "clave123")) } returns respuesta
        e.llenarLogin()
        var sesionCerrada = false

        runTest {
            e.viewModel.iniciarSesion { }
            e.viewModel.usuarioActual.value shouldBe Usuario("Matías", "matias@correo.cl", "+56912345678", "")
            SesionApi.token shouldBe "token-abc"

            e.viewModel.cerrarSesion { sesionCerrada = true }

            sesionCerrada shouldBe true
            e.viewModel.usuarioActual.value shouldBe null
            SesionApi.token shouldBe null
            coVerify { e.prefs.guardarSesion(null) }
            coVerify { e.prefs.guardarToken(null) }
        }
    }

    "registrarse() debe enviar los datos normalizados, mostrar el éxito y limpiar el formulario" {
        val e = EscenarioCuenta()
        coEvery { e.api.registrar(any()) } returns UsuarioDto("Matías Pérez", "matias@correo.cl", "+56912345678", "cliente")
        e.llenarRegistroValido()
        var exito = false

        runTest {
            e.viewModel.registrarse { exito = true }

            e.viewModel.registroExitoso.value shouldBe true
            exito shouldBe false

            advanceUntilIdle()

            e.viewModel.registroExitoso.value shouldBe false
            exito shouldBe true
            e.viewModel.registro.value shouldBe RegistroUiState()
            coVerify { e.api.registrar(RegistroBody("Matías Pérez", "matias@correo.cl", "+56912345678", "clave123")) }
        }
    }

    "registrarse() debe mostrar el error en el campo correo cuando la API falla" {
        val e = EscenarioCuenta()
        coEvery { e.api.registrar(any()) } throws IOException("sin red")
        e.llenarRegistroValido()
        var exito = false

        runTest {
            e.viewModel.registrarse { exito = true }

            exito shouldBe false
            e.viewModel.registroExitoso.value shouldBe false
            e.viewModel.registro.value.errores.correo shouldBe MENSAJE_SIN_CONEXION
        }
    }

    "registrarse() no debe llamar a la API cuando el formulario está incompleto" {
        val e = EscenarioCuenta()
        var exito = false

        runTest {
            e.viewModel.registrarse { exito = true }

            exito shouldBe false
            e.viewModel.registro.value.errores.nombre shouldBe "Campo obligatorio"
            coVerify(exactly = 0) { e.api.registrar(any()) }
        }
    }

    "registrarse() debe ignorar un segundo toque mientras se muestra el mensaje de éxito" {
        val e = EscenarioCuenta()
        coEvery { e.api.registrar(any()) } returns UsuarioDto("Matías Pérez", "matias@correo.cl", "+56912345678", "cliente")
        e.llenarRegistroValido()
        var veces = 0

        runTest {
            e.viewModel.registrarse { veces++ }
            e.viewModel.registrarse { veces++ }
            advanceUntilIdle()

            veces shouldBe 1
            coVerify(exactly = 1) { e.api.registrar(any()) }
        }
    }
})