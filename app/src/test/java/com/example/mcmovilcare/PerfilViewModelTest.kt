package com.example.mcmovilcare

import android.app.Application
import android.net.Uri
import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.viewmodels.PerfilViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModelTest : StringSpec({

    val testDispatcher = UnconfinedTestDispatcher()

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Uri::class)
    }

    afterTest {
        Dispatchers.resetMain()
        unmockkStatic(Uri::class)
    }

    "cargarFoto() debe actualizar el StateFlow imagen cuando existe una ruta guardada en DataStore" {
        val fakeUri = mockk<Uri>(relaxed = true)
        every { Uri.parse("file:///storage/emulated/0/perfil.jpg") } returns fakeUri

        val mockApp = mockk<Application>(relaxed = true)
        val mockPrefs = mockk<PreferenciasDataStore>(relaxed = true) {
            every { obtenerFoto("matias@correo.cl") } returns flowOf("file:///storage/emulated/0/perfil.jpg")
        }

        val viewModel = PerfilViewModel(application = mockApp, prefs = mockPrefs)

        runTest {
            viewModel.cargarFoto("matias@correo.cl")
            viewModel.imagen.value shouldBe fakeUri
        }
    }

    "limpiar() debe dejar el StateFlow imagen en null" {
        val fakeUri = mockk<Uri>(relaxed = true)
        every { Uri.parse(any()) } returns fakeUri

        val mockApp = mockk<Application>(relaxed = true)
        val mockPrefs = mockk<PreferenciasDataStore>(relaxed = true) {
            every { obtenerFoto("matias@correo.cl") } returns flowOf("file:///perfil.jpg")
        }

        val viewModel = PerfilViewModel(application = mockApp, prefs = mockPrefs)

        runTest {
            viewModel.cargarFoto("matias@correo.cl")
            viewModel.imagen.value shouldBe fakeUri

            viewModel.limpiar()
            viewModel.imagen.value shouldBe null
        }
    }
})