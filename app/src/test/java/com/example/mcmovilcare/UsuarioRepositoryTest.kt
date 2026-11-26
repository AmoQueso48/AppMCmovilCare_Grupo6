package com.example.mcmovilcare

import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.data.remote.ApiService
import com.example.mcmovilcare.model.Usuario
import com.example.mcmovilcare.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class UsuarioRepositoryTest : StringSpec({

    "login() debe guardar token y usuario en DataStore y retornar el Usuario" {
        val mockPrefs = mockk<PreferenciasDataStore>(relaxed = true)
        val mockApi = mockk<ApiService>()

        // Simulamos la respuesta de api.login(...) usando relaxed mock
        val fakeRespuesta = mockk<Any>(relaxed = true)
        coEvery { mockApi.login(any()) } returns mockk(relaxed = true) {
            every { token } returns "jwt-token-123"
            every { usuario.nombre } returns "Matías"
            every { usuario.correo } returns "matias@correo.cl"
            every { usuario.telefono } returns "+56912345678"
        }

        val repo = UsuarioRepository(prefs = mockPrefs, api = mockApi)

        runTest {
            val resultado = repo.login("matias@correo.cl", "123456")

            resultado.isSuccess shouldBe true
            resultado.getOrNull()?.correo shouldBe "matias@correo.cl"
            resultado.getOrNull()?.nombre shouldBe "Matías"
            coVerify { mockPrefs.guardarToken("jwt-token-123") }
        }
    }

    "restaurarSesion() debe retornar el usuario si existe sesión guardada en DataStore" {
        val usuarioGuardado = Usuario("Matías", "matias@correo.cl", "+56912345678", "")
        val mockPrefs = mockk<PreferenciasDataStore>(relaxed = true) {
            every { obtenerSesion() } returns flowOf("matias@correo.cl")
            every { obtenerToken() } returns flowOf("jwt-token-123")
            every { obtenerUsuarioRegistrado() } returns flowOf(usuarioGuardado)
        }
        val mockApi = mockk<ApiService>()

        val repo = UsuarioRepository(prefs = mockPrefs, api = mockApi)

        runTest {
            val usuarioRestaurado = repo.restaurarSesion()
            usuarioRestaurado shouldBe usuarioGuardado
        }
    }
})