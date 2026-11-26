package com.example.mcmovilcare

import android.app.Application
import com.example.mcmovilcare.model.EstadoSolicitud
import com.example.mcmovilcare.model.SolicitudDeAtencion
import com.example.mcmovilcare.repository.SolicitudRepository
import com.example.mcmovilcare.viewmodels.SolicitudViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class SolicitudViewModelTest : StringSpec({

    "solicitudesDe() debe entregar las solicitudes simuladas del repositorio" {
        // 1. Datos falsos
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
            )
        )

        // 2. Simulamos Application y SolicitudRepository con MockK
        val mockApp = mockk<Application>(relaxed = true)
        val mockRepo = mockk<SolicitudRepository>()
        every { mockRepo.solicitudesDe("cliente1@correo.cl") } returns flowOf(fakeSolicitudes)

        // 3. Instanciamos el ViewModel real con los mocks
        val viewModel = SolicitudViewModel(application = mockApp, repo = mockRepo)

        // 4. Ejecutamos y verificamos
        runTest {
            val resultado = viewModel.solicitudesDe("cliente1@correo.cl").first()
            resultado shouldContainExactly fakeSolicitudes
        }
    }

    "validarFormulario() debe retornar false si los campos están vacíos y true cuando son válidos" {
        val mockApp = mockk<Application>(relaxed = true)
        val mockRepo = mockk<SolicitudRepository>()
        val viewModel = SolicitudViewModel(application = mockApp, repo = mockRepo)

        // Con el estado inicial vacío debe fallar la validación
        viewModel.validarFormulario() shouldBe false

        // Llenamos los campos correctamente
        viewModel.onEquipoChange(101)
        viewModel.onTipoServicioChange("Mantenimiento")
        viewModel.onPrioridadChange("Alta")
        viewModel.onDescripcionChange("El equipo presenta fallas al iniciar el sistema")

        // Ahora debe pasar la validación
        viewModel.validarFormulario() shouldBe true
    }
})