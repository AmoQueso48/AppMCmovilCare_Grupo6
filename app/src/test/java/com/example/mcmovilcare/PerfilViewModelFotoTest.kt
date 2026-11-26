package com.example.mcmovilcare

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.viewmodels.PerfilViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files

private const val CORREO = "matias@correo.cl"

private fun esperarHasta(maxMs: Long = 3000, condicion: () -> Boolean) {
    val limite = System.currentTimeMillis() + maxMs
    while (!condicion() && System.currentTimeMillis() < limite) Thread.sleep(20)
}

private class EscenarioFoto {
    val carpeta: File = Files.createTempDirectory("perfil_test").toFile()
    val resolver = mockk<ContentResolver>()
    val app = mockk<Application>(relaxed = true).also {
        every { it.filesDir } returns carpeta
        every { it.contentResolver } returns resolver
    }
    val prefs = mockk<PreferenciasDataStore>(relaxed = true).also {
        every { it.obtenerFoto(any()) } returns flowOf<String?>(null)
    }
    val viewModel = PerfilViewModel(application = app, prefs = prefs)
    val origen = mockk<Uri>(relaxed = true)
    val fotoGuardada = mockk<Uri>(relaxed = true)

    init {
        every { Uri.fromFile(any()) } returns fotoGuardada
    }

    fun limpiar() {
        carpeta.deleteRecursively()
    }
}

private fun <T> conEscenario(bloque: (EscenarioFoto) -> T): T {
    val escenario = EscenarioFoto()
    try {
        return bloque(escenario)
    } finally {
        escenario.limpiar()
    }
}

private fun probarCopiaExitosa(accion: (PerfilViewModel, Uri) -> Unit) {
    conEscenario { e ->
        every { e.resolver.openInputStream(e.origen) } returns ByteArrayInputStream("contenido-foto".toByteArray())
        e.viewModel.cargarFoto(CORREO)

        accion(e.viewModel, e.origen)
        esperarHasta { e.viewModel.imagen.value != null }

        e.viewModel.imagen.value shouldBe e.fotoGuardada
        coVerify(timeout = 3000) { e.prefs.guardarFoto(CORREO, any()) }
        e.carpeta.listFiles()!!.single().readText() shouldBe "contenido-foto"
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModelFotoTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Uri::class)
    }

    afterTest {
        Dispatchers.resetMain()
        unmockkStatic(Uri::class)
    }

    "cargarFoto() debe dejar la imagen en null cuando no hay foto guardada para el usuario" {
        conEscenario { e ->
            e.viewModel.cargarFoto(CORREO)

            e.viewModel.imagen.value shouldBe null
            verify(exactly = 0) { Uri.parse(any()) }
        }
    }

    "actualizarDesdeGaleria() debe copiar la foto, mostrarla y guardar su ruta en DataStore" {
        probarCopiaExitosa { vm, uri -> vm.actualizarDesdeGaleria(uri) }
    }

    "actualizarDesdeCamara() debe copiar la foto, mostrarla y guardar su ruta en DataStore" {
        probarCopiaExitosa { vm, uri -> vm.actualizarDesdeCamara(uri) }
    }

    "actualizarDesdeGaleria() no debe hacer nada si todavía no se cargó la foto de un usuario" {
        conEscenario { e ->
            e.viewModel.actualizarDesdeGaleria(e.origen)

            e.viewModel.imagen.value shouldBe null
            verify(exactly = 0) { e.resolver.openInputStream(any()) }
            coVerify(exactly = 0) { e.prefs.guardarFoto(any(), any()) }
        }
    }

    "actualizarDesdeGaleria() no debe cambiar la foto si no se puede abrir el archivo de origen" {
        conEscenario { e ->
            every { e.resolver.openInputStream(any()) } returns null
            e.viewModel.cargarFoto(CORREO)

            e.viewModel.actualizarDesdeGaleria(e.origen)
            verify(timeout = 3000) { e.resolver.openInputStream(any()) }
            Thread.sleep(200)

            e.viewModel.imagen.value shouldBe null
            coVerify(exactly = 0) { e.prefs.guardarFoto(any(), any()) }
        }
    }

    "actualizarDesdeCamara() no debe cambiar la foto si ocurre un error al copiar el archivo" {
        conEscenario { e ->
            every { e.resolver.openInputStream(any()) } throws SecurityException("sin permiso")
            e.viewModel.cargarFoto(CORREO)

            e.viewModel.actualizarDesdeCamara(e.origen)
            verify(timeout = 3000) { e.resolver.openInputStream(any()) }
            Thread.sleep(200)

            e.viewModel.imagen.value shouldBe null
            coVerify(exactly = 0) { e.prefs.guardarFoto(any(), any()) }
        }
    }
})