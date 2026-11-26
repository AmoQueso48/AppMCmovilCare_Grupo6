package com.example.mcmovilcare

import android.app.Application
import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.repository.EquipoRepository
import com.example.mcmovilcare.viewmodels.EquiposViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class EquiposViewModelTest : StringSpec({

    val testDispatcher = UnconfinedTestDispatcher()

    beforeTest {
        Dispatchers.setMain(testDispatcher)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "cargarEquipos() debe actualizar el StateFlow equipos cuando el repositorio responde con éxito" {
        val fakeEquipo = mockk<Equipo>(relaxed = true)
        val mockApp = mockk<Application>(relaxed = true)
        val mockRepo = mockk<EquipoRepository>(relaxed = true) {
            every { ordenes } returns emptyFlow()
            coEvery { cargar() } returns Result.success(listOf(fakeEquipo))
        }

        val viewModel = EquiposViewModel(application = mockApp, repo = mockRepo)

        runTest {
            viewModel.cargarEquipos("cliente1@correo.cl")

            viewModel.cargando.value shouldBe false
            viewModel.error.value shouldBe null
            viewModel.equipos.value shouldContainExactly listOf(fakeEquipo)
        }
    }

    "validarMotivo() debe retornar false si está vacío o es muy corto y true cuando tiene al menos 10 caracteres" {
        val mockApp = mockk<Application>(relaxed = true)
        val mockRepo = mockk<EquipoRepository>(relaxed = true) {
            every { ordenes } returns emptyFlow()
        }

        val viewModel = EquiposViewModel(application = mockApp, repo = mockRepo)

        // Vacío -> falla
        viewModel.validarMotivo() shouldBe false
        viewModel.autorizacion.value.errores.motivo shouldBe "Indica el motivo del rechazo"

        // Menos de 10 caracteres -> falla
        viewModel.onMotivoChange("Muy caro")
        viewModel.validarMotivo() shouldBe false

        // 10 o más caracteres -> pasa
        viewModel.onMotivoChange("El presupuesto excede lo acordado previamente")
        viewModel.validarMotivo() shouldBe true
        viewModel.autorizacion.value.errores.motivo shouldBe null
    }
})