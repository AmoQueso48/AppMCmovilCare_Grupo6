package com.example.mcmovilcare

import android.app.Application
import com.example.mcmovilcare.model.Usuario
import com.example.mcmovilcare.repository.UsuarioRepository
import com.example.mcmovilcare.viewmodels.UsuarioViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest : StringSpec({

    val testDispatcher = UnconfinedTestDispatcher()

    beforeTest {
        Dispatchers.setMain(testDispatcher)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "validarLogin() debe validar formato de correo y clave obligatoria" {
        val mockApp = mockk<Application>(relaxed = true)
        val mockRepo = mockk<UsuarioRepository>(relaxed = true)
        val viewModel = UsuarioViewModel(application = mockApp, repo = mockRepo)

        // Campos vacíos -> falla
        viewModel.validarLogin() shouldBe false
        viewModel.login.value.errores.correo shouldBe "Campo obligatorio"
        viewModel.login.value.errores.clave shouldBe "Ingresa tu clave"

        // Correo sin @ -> falla
        viewModel.onLoginCorreoChange("correo_invalido")
        viewModel.onLoginClaveChange("123456")
        viewModel.validarLogin() shouldBe false
        viewModel.login.value.errores.correo shouldBe "Correo inválido"

        // Datos válidos -> pasa
        viewModel.onLoginCorreoChange("cliente@correo.cl")
        viewModel.validarLogin() shouldBe true
        viewModel.login.value.errores.correo shouldBe null
        viewModel.login.value.errores.clave shouldBe null
    }

    "validarRegistro() debe validar todos los campos del formulario de registro" {
        val mockApp = mockk<Application>(relaxed = true)
        val mockRepo = mockk<UsuarioRepository>(relaxed = true)
        val viewModel = UsuarioViewModel(application = mockApp, repo = mockRepo)

        // Vacío -> falla
        viewModel.validarRegistro() shouldBe false

        // con datos válidos
        viewModel.onRegistroNombreChange("Matías Pérez")
        viewModel.onRegistroCorreoChange("matias@correo.cl")
        viewModel.onRegistroTelefonoChange("+56912345678")
        viewModel.onRegistroClaveChange("clave123")
        viewModel.onRegistroRepetirClaveChange("clave123")
        viewModel.onRegistroTerminosChange(true)

        viewModel.validarRegistro() shouldBe true
    }

    "iniciarSesion() debe actualizar usuarioActual y ejecutar onExito cuando el login es correcto" {
        val fakeUsuario = Usuario("Matías", "matias@correo.cl", "+56912345678", "")
        val mockApp = mockk<Application>(relaxed = true)
        val mockRepo = mockk<UsuarioRepository>(relaxed = true) {
            coEvery { login("matias@correo.cl", "clave123") } returns Result.success(fakeUsuario)
        }

        val viewModel = UsuarioViewModel(application = mockApp, repo = mockRepo)
        viewModel.onLoginCorreoChange("matias@correo.cl")
        viewModel.onLoginClaveChange("clave123")

        var navegoAlHome = false
        runTest {
            viewModel.iniciarSesion(onExito = { navegoAlHome = true })

            viewModel.usuarioActual.value shouldBe fakeUsuario
            navegoAlHome shouldBe true
            coVerify { mockRepo.guardarSesion("matias@correo.cl") }
        }
    }
})