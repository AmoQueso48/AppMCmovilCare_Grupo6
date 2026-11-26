package com.example.mcmovilcare

import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.data.remote.ApiService
import com.example.mcmovilcare.data.remote.LoginBody
import com.example.mcmovilcare.data.remote.LoginRespuesta
import com.example.mcmovilcare.data.remote.RegistroBody
import com.example.mcmovilcare.data.remote.SesionApi
import com.example.mcmovilcare.data.remote.UsuarioDto
import com.example.mcmovilcare.model.Usuario
import com.example.mcmovilcare.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import java.io.IOException

class UsuarioRepositoryCuentaTest : StringSpec({

    val usuarioDto = UsuarioDto("Matías", "matias@correo.cl", "+56912345678", "cliente")

    beforeTest { SesionApi.token = null }
    afterTest { SesionApi.token = null }

    "login() debe recortar el correo, dejar el token en la sesión y guardar el usuario sin clave" {
        val prefs = mockk<PreferenciasDataStore>(relaxed = true)
        val api = mockk<ApiService>()
        coEvery { api.login(LoginBody("matias@correo.cl", "clave123")) } returns LoginRespuesta("token-abc", usuarioDto)
        val repo = UsuarioRepository(prefs = prefs, api = api)

        runTest {
            val resultado = repo.login("  matias@correo.cl ", "clave123")

            val esperado = Usuario("Matías", "matias@correo.cl", "+56912345678", "")
            resultado.isSuccess shouldBe true
            resultado.getOrNull() shouldBe esperado
            SesionApi.token shouldBe "token-abc"
            coVerify { prefs.guardarToken("token-abc") }
            coVerify { prefs.guardarUsuario(esperado) }
        }
    }

    "login() debe retornar falla y no guardar nada cuando la API no responde" {
        val prefs = mockk<PreferenciasDataStore>(relaxed = true)
        val api = mockk<ApiService>()
        coEvery { api.login(any()) } throws IOException("sin red")
        val repo = UsuarioRepository(prefs = prefs, api = api)

        runTest {
            val resultado = repo.login("matias@correo.cl", "clave123")

            resultado.isFailure shouldBe true
            resultado.exceptionOrNull()?.message shouldBe "sin red"
            SesionApi.token shouldBe null
            coVerify(exactly = 0) { prefs.guardarToken(any()) }
            coVerify(exactly = 0) { prefs.guardarUsuario(any()) }
        }
    }

    "registrar() debe enviar los datos a la API y retornar éxito" {
        val prefs = mockk<PreferenciasDataStore>(relaxed = true)
        val api = mockk<ApiService>()
        coEvery { api.registrar(any()) } returns usuarioDto
        val repo = UsuarioRepository(prefs = prefs, api = api)

        runTest {
            val resultado = repo.registrar(Usuario("Matías", "matias@correo.cl", "+56912345678", "clave123"))

            resultado.isSuccess shouldBe true
            coVerify { api.registrar(RegistroBody("Matías", "matias@correo.cl", "+56912345678", "clave123")) }
        }
    }

    "registrar() debe retornar falla cuando la API rechaza o no responde" {
        val prefs = mockk<PreferenciasDataStore>(relaxed = true)
        val api = mockk<ApiService>()
        coEvery { api.registrar(any()) } throws IOException("sin red")
        val repo = UsuarioRepository(prefs = prefs, api = api)

        runTest {
            val resultado = repo.registrar(Usuario("Matías", "matias@correo.cl", "+56912345678", "clave123"))

            resultado.isFailure shouldBe true
            resultado.exceptionOrNull()?.message shouldBe "sin red"
        }
    }

    "usuarioPorCorreo() debe encontrar al usuario sin distinguir mayúsculas y retornar null si el correo es otro" {
        val guardado = Usuario("Matías", "matias@correo.cl", "+56912345678", "")
        val prefs = mockk<PreferenciasDataStore>(relaxed = true) {
            every { obtenerUsuarioRegistrado() } returns flowOf(guardado)
        }
        val repo = UsuarioRepository(prefs = prefs, api = mockk())

        runTest {
            repo.usuarioPorCorreo("MATIAS@CORREO.CL") shouldBe guardado
            repo.usuarioPorCorreo("otro@correo.cl") shouldBe null
        }
    }

    "restaurarSesion() debe retornar null y no tocar el token cuando no hay sesión guardada" {
        val prefs = mockk<PreferenciasDataStore>(relaxed = true) {
            every { obtenerSesion() } returns flowOf<String?>(null)
        }
        val repo = UsuarioRepository(prefs = prefs, api = mockk())

        runTest {
            repo.restaurarSesion() shouldBe null
            SesionApi.token shouldBe null
            verify(exactly = 0) { prefs.obtenerToken() }
        }
    }

    "restaurarSesion() debe dejar el token guardado en la sesión HTTP" {
        val guardado = Usuario("Matías", "matias@correo.cl", "+56912345678", "")
        val prefs = mockk<PreferenciasDataStore>(relaxed = true) {
            every { obtenerSesion() } returns flowOf<String?>("matias@correo.cl")
            every { obtenerToken() } returns flowOf<String?>("token-guardado")
            every { obtenerUsuarioRegistrado() } returns flowOf(guardado)
        }
        val repo = UsuarioRepository(prefs = prefs, api = mockk())

        runTest {
            repo.restaurarSesion() shouldBe guardado
            SesionApi.token shouldBe "token-guardado"
        }
    }

    "guardarSesion() debe delegar el correo al DataStore" {
        val prefs = mockk<PreferenciasDataStore>(relaxed = true)
        val repo = UsuarioRepository(prefs = prefs, api = mockk())

        runTest {
            repo.guardarSesion("matias@correo.cl")

            coVerify { prefs.guardarSesion("matias@correo.cl") }
        }
    }

    "cerrarSesion() debe borrar la sesión y el token guardados y limpiar el token en memoria" {
        val prefs = mockk<PreferenciasDataStore>(relaxed = true)
        val repo = UsuarioRepository(prefs = prefs, api = mockk())
        SesionApi.token = "token-abc"

        runTest {
            repo.cerrarSesion()

            coVerify { prefs.guardarSesion(null) }
            coVerify { prefs.guardarToken(null) }
            SesionApi.token shouldBe null
        }
    }
})